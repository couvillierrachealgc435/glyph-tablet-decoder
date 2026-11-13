package com.glyphtablet.decoder.data.local

import android.content.Context
import com.glyphtablet.decoder.data.sample.SampleData
import com.glyphtablet.decoder.domain.game.CipherRules
import com.glyphtablet.decoder.domain.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class ArchivePreferences(context: Context) {
    private val preferences = context.getSharedPreferences("glyph_tablet_decoder_v1", Context.MODE_PRIVATE)

    fun read(): ArchiveSnapshot {
        val raw = preferences.getString("archive_snapshot", null) ?: return ArchiveSnapshot()
        val root = JSONObject(raw)
        require(root.getInt("schemaVersion") == 1)
        val completions = buildMap {
            val values = root.optJSONObject("completions") ?: JSONObject()
            values.keys().forEach { key ->
                runCatching {
                    val id = key.toInt()
                    val value = values.getJSONObject(key)
                    require(id in 1..200 && value.getLong("time") > 0 && value.getInt("hints") in 0..2)
                    put(id, Completion(value.getLong("time"), value.getInt("hints")))
                }
            }
        }
        var recovered = false
        val sessions = buildMap {
            val values = root.optJSONArray("sessions") ?: JSONArray()
            for (index in 0 until values.length()) {
                runCatching {
                    val value = values.getJSONObject(index)
                    val id = value.getInt("tablet")
                    require(id in 1..200)
                    val tablet = SampleData.tablets[id - 1]
                    val assignments = buildMap {
                        val pairs = value.getJSONObject("assignments")
                        pairs.keys().forEach { key ->
                            val glyph = key.toInt()
                            val letter = pairs.getString(key).single()
                            require(glyph in CipherRules.used(tablet) && letter == CipherRules.letter(glyph, tablet.chapter))
                            put(glyph, letter)
                        }
                    }
                    val rejected = buildMap {
                        val pairs = value.getJSONObject("rejected")
                        pairs.keys().forEach { key ->
                            val glyph = key.toInt()
                            val letters = pairs.getString(key).toSet()
                            require(glyph in CipherRules.used(tablet) && letters.all { it in 'A'..'Z' && it != CipherRules.letter(glyph, tablet.chapter) })
                            put(glyph, letters)
                        }
                    }
                    val hints = value.getInt("hints")
                    val time = value.getLong("time")
                    require(hints in 0..2 && time >= 0)
                    val date = value.optString("date").takeIf { it.isNotBlank() }?.also { LocalDate.parse(it) }
                    val board = BoardSession(
                        boardId = value.getString("id"), tabletId = id,
                        mode = BoardMode.valueOf(value.getString("mode")), dailyDate = date,
                        assignments = assignments, rejected = rejected, hintsRemaining = hints, elapsedMs = time,
                        selected = value.optInt("selected", -1).takeIf { it in CipherRules.used(tablet) && it !in assignments },
                        phase = if (value.optBoolean("solved")) BoardPhase.Solved else BoardPhase.Paused,
                        attemptId = value.optString("attempt").takeIf { it.isNotBlank() }
                    )
                    require(board.boardId.isNotBlank())
                    require(board.phase != BoardPhase.Solved || CipherRules.isSolved(tablet, board))
                    put(board.boardId, board)
                }.onFailure { recovered = true }
            }
        }
        val learned = buildMap {
            val chapters = root.optJSONObject("learned") ?: JSONObject()
            chapters.keys().forEach { chapterKey ->
                runCatching {
                    val chapter = chapterKey.toInt()
                    require(chapter in 0..9)
                    val entries = chapters.getJSONObject(chapterKey)
                    val pairs = buildMap {
                        entries.keys().forEach { key ->
                            runCatching {
                                val glyph = key.toInt()
                                val source = entries.getInt(key)
                                require(glyph in 0..25 && source in 1..200 && (source - 1) / 20 == chapter)
                                put(glyph, LearnedPair(CipherRules.letter(glyph, chapter), source))
                            }
                        }
                    }
                    put(chapter, pairs)
                }
            }
        }
        val dates = root.optJSONArray("daily") ?: JSONArray()
        val daily = (0 until dates.length()).mapNotNull { runCatching { LocalDate.parse(dates.getString(it)).toString() }.getOrNull() }.toSet()
        return ArchiveSnapshot(
            sessions = sessions, completions = completions, learned = learned, dailyDates = daily,
            settings = AppSettings(root.optBoolean("large"), root.optBoolean("sound", true)),
            lastActive = root.optInt("last", -1).takeIf { it in 1..200 },
            codexChapter = root.optInt("chapter", 0).coerceIn(0, 9), recoveredBoard = recovered
        )
    }

    fun write(snapshot: ArchiveSnapshot) {
        val root = JSONObject().put("schemaVersion", 1)
        val completions = JSONObject()
        snapshot.completions.forEach { (id, record) -> completions.put(id.toString(), JSONObject().put("time", record.elapsedMs).put("hints", record.hintsUsed)) }
        val sessions = JSONArray()
        snapshot.sessions.values.forEach { board ->
            val assignments = JSONObject()
            board.assignments.forEach { (glyph, letter) -> assignments.put(glyph.toString(), letter.toString()) }
            val rejected = JSONObject()
            board.rejected.forEach { (glyph, letters) -> rejected.put(glyph.toString(), letters.sorted().joinToString("")) }
            sessions.put(JSONObject().put("id", board.boardId).put("tablet", board.tabletId).put("mode", board.mode.name)
                .put("date", board.dailyDate.orEmpty()).put("assignments", assignments).put("rejected", rejected)
                .put("hints", board.hintsRemaining).put("time", board.elapsedMs).put("selected", board.selected ?: -1)
                .put("solved", board.phase == BoardPhase.Solved).put("attempt", board.attemptId.orEmpty()))
        }
        val learned = JSONObject()
        snapshot.learned.forEach { (chapter, pairs) ->
            val values = JSONObject()
            pairs.forEach { (glyph, pair) -> values.put(glyph.toString(), pair.tabletId) }
            learned.put(chapter.toString(), values)
        }
        root.put("completions", completions).put("sessions", sessions).put("learned", learned)
            .put("daily", JSONArray(snapshot.dailyDates.sorted())).put("large", snapshot.settings.largeText)
            .put("sound", snapshot.settings.soundEnabled).put("last", snapshot.lastActive ?: -1).put("chapter", snapshot.codexChapter)
        check(preferences.edit().putString("archive_snapshot", root.toString()).commit())
    }

    fun clear() { check(preferences.edit().clear().commit()) }
}
