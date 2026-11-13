package com.glyphtablet.decoder.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.glyphtablet.decoder.R

object TextResources {
    val proverbs = listOf(R.string.proverb_001,R.string.proverb_002,R.string.proverb_003,R.string.proverb_004,R.string.proverb_005,R.string.proverb_006,R.string.proverb_007,R.string.proverb_008,R.string.proverb_009,R.string.proverb_010,R.string.proverb_011,R.string.proverb_012,R.string.proverb_013,R.string.proverb_014,R.string.proverb_015,R.string.proverb_016,R.string.proverb_017,R.string.proverb_018,R.string.proverb_019,R.string.proverb_020,R.string.proverb_021,R.string.proverb_022,R.string.proverb_023,R.string.proverb_024,R.string.proverb_025,R.string.proverb_026,R.string.proverb_027,R.string.proverb_028,R.string.proverb_029,R.string.proverb_030,R.string.proverb_031,R.string.proverb_032,R.string.proverb_033,R.string.proverb_034,R.string.proverb_035,R.string.proverb_036,R.string.proverb_037,R.string.proverb_038,R.string.proverb_039,R.string.proverb_040,R.string.proverb_041,R.string.proverb_042,R.string.proverb_043,R.string.proverb_044,R.string.proverb_045,R.string.proverb_046,R.string.proverb_047,R.string.proverb_048,R.string.proverb_049,R.string.proverb_050,R.string.proverb_051,R.string.proverb_052,R.string.proverb_053,R.string.proverb_054,R.string.proverb_055,R.string.proverb_056,R.string.proverb_057,R.string.proverb_058,R.string.proverb_059,R.string.proverb_060,R.string.proverb_061,R.string.proverb_062,R.string.proverb_063,R.string.proverb_064,R.string.proverb_065,R.string.proverb_066,R.string.proverb_067,R.string.proverb_068,R.string.proverb_069,R.string.proverb_070,R.string.proverb_071,R.string.proverb_072,R.string.proverb_073,R.string.proverb_074,R.string.proverb_075,R.string.proverb_076,R.string.proverb_077,R.string.proverb_078,R.string.proverb_079,R.string.proverb_080,R.string.proverb_081,R.string.proverb_082,R.string.proverb_083,R.string.proverb_084,R.string.proverb_085,R.string.proverb_086,R.string.proverb_087,R.string.proverb_088,R.string.proverb_089,R.string.proverb_090,R.string.proverb_091,R.string.proverb_092,R.string.proverb_093,R.string.proverb_094,R.string.proverb_095,R.string.proverb_096,R.string.proverb_097,R.string.proverb_098,R.string.proverb_099,R.string.proverb_100,R.string.proverb_101,R.string.proverb_102,R.string.proverb_103,R.string.proverb_104,R.string.proverb_105,R.string.proverb_106,R.string.proverb_107,R.string.proverb_108,R.string.proverb_109,R.string.proverb_110,R.string.proverb_111,R.string.proverb_112,R.string.proverb_113,R.string.proverb_114,R.string.proverb_115,R.string.proverb_116,R.string.proverb_117,R.string.proverb_118,R.string.proverb_119,R.string.proverb_120,R.string.proverb_121,R.string.proverb_122,R.string.proverb_123,R.string.proverb_124,R.string.proverb_125,R.string.proverb_126,R.string.proverb_127,R.string.proverb_128,R.string.proverb_129,R.string.proverb_130,R.string.proverb_131,R.string.proverb_132,R.string.proverb_133,R.string.proverb_134,R.string.proverb_135,R.string.proverb_136,R.string.proverb_137,R.string.proverb_138,R.string.proverb_139,R.string.proverb_140,R.string.proverb_141,R.string.proverb_142,R.string.proverb_143,R.string.proverb_144,R.string.proverb_145,R.string.proverb_146,R.string.proverb_147,R.string.proverb_148,R.string.proverb_149,R.string.proverb_150,R.string.proverb_151,R.string.proverb_152,R.string.proverb_153,R.string.proverb_154,R.string.proverb_155,R.string.proverb_156,R.string.proverb_157,R.string.proverb_158,R.string.proverb_159,R.string.proverb_160,R.string.proverb_161,R.string.proverb_162,R.string.proverb_163,R.string.proverb_164,R.string.proverb_165,R.string.proverb_166,R.string.proverb_167,R.string.proverb_168,R.string.proverb_169,R.string.proverb_170,R.string.proverb_171,R.string.proverb_172,R.string.proverb_173,R.string.proverb_174,R.string.proverb_175,R.string.proverb_176,R.string.proverb_177,R.string.proverb_178,R.string.proverb_179,R.string.proverb_180,R.string.proverb_181,R.string.proverb_182,R.string.proverb_183,R.string.proverb_184,R.string.proverb_185,R.string.proverb_186,R.string.proverb_187,R.string.proverb_188,R.string.proverb_189,R.string.proverb_190,R.string.proverb_191,R.string.proverb_192,R.string.proverb_193,R.string.proverb_194,R.string.proverb_195,R.string.proverb_196,R.string.proverb_197,R.string.proverb_198,R.string.proverb_199,R.string.proverb_200)
    val chapters = listOf(R.string.chapter_0,R.string.chapter_1,R.string.chapter_2,R.string.chapter_3,R.string.chapter_4,R.string.chapter_5,R.string.chapter_6,R.string.chapter_7,R.string.chapter_8,R.string.chapter_9)
    val letters = listOf(R.string.letter_a,R.string.letter_b,R.string.letter_c,R.string.letter_d,R.string.letter_e,R.string.letter_f,R.string.letter_g,R.string.letter_h,R.string.letter_i,R.string.letter_j,R.string.letter_k,R.string.letter_l,R.string.letter_m,R.string.letter_n,R.string.letter_o,R.string.letter_p,R.string.letter_q,R.string.letter_r,R.string.letter_s,R.string.letter_t,R.string.letter_u,R.string.letter_v,R.string.letter_w,R.string.letter_x,R.string.letter_y,R.string.letter_z)
    fun originTitle(key: String): Int = when (key) {
        "AT" -> R.string.origin_at_title
        "AL" -> R.string.origin_al_title
        "AW" -> R.string.origin_aw_title
        "AG" -> R.string.origin_ag_title
        "L" -> R.string.origin_l_title
        "P" -> R.string.origin_p_title
        "E" -> R.string.origin_e_title
        "M" -> R.string.origin_m_title
        "S" -> R.string.origin_s_title
        else -> R.string.origin_t_title
    }
    fun originNote(key: String): Int = when (key) {
        "AT" -> R.string.origin_at_note
        "AL" -> R.string.origin_al_note
        "AW" -> R.string.origin_aw_note
        "AG" -> R.string.origin_ag_note
        "L" -> R.string.origin_l_note
        "P" -> R.string.origin_p_note
        "E" -> R.string.origin_e_note
        "M" -> R.string.origin_m_note
        "S" -> R.string.origin_s_note
        else -> R.string.origin_t_note
    }
}

@Composable
fun timeText(milliseconds: Long): String {
    val seconds = ((milliseconds + 999) / 1000).coerceAtLeast(0)
    return if (seconds >= 3600) stringResource(R.string.long_time_format, seconds / 3600, seconds / 60 % 60, seconds % 60)
    else stringResource(R.string.time_format, seconds / 60, seconds % 60)
}
