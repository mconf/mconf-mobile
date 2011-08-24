#include <error.h>
#include "gtest_EncodeVideoH264Presets.h"

TEST_F(EncodeVideoH264PresetsTest, ParseWithWrongParameters)
{
    int err;

    err = _presets->parse("", NULL);
    EXPECT_NE(err, E_OK);

    err = _presets->parse("", _codecCtx);
    EXPECT_NE(err, E_OK);

    err = _presets->parse(_presetFile, NULL);
    EXPECT_NE(err, E_OK);
}

TEST_F(EncodeVideoH264PresetsTest, ParseAndCheckValues)
{
    int err;

    err = _presets->parse(_presetFile, _codecCtx);
    ASSERT_EQ(err, E_OK);

    // confere se os valores do arquivo foram setados no contexto mesmo
    EXPECT_NE(_codecCtx->me_cmp & FF_CMP_CHROMA, 0);
    EXPECT_EQ(_codecCtx->me_method, ME_HEX);
    EXPECT_NE(_codecCtx->flags2 & CODEC_FLAG2_WPRED, 0);
    EXPECT_NE(_codecCtx->flags2 & CODEC_FLAG2_8X8DCT, 0);
    EXPECT_EQ(_codecCtx->flags2 & CODEC_FLAG2_FASTPSKIP, 0);
    EXPECT_NE(_codecCtx->flags2 & CODEC_FLAG2_BPYRAMID, 0);
    EXPECT_EQ(_codecCtx->flags2 & CODEC_FLAG2_MIXED_REFS, 0);
    EXPECT_EQ(_codecCtx->flags2 & CODEC_FLAG2_MBTREE, 0);
    EXPECT_NE(_codecCtx->partitions & X264_PART_I8X8, 0);
    EXPECT_NE(_codecCtx->partitions & X264_PART_I4X4, 0);
    EXPECT_EQ(_codecCtx->partitions & X264_PART_P8X8, 0);
    EXPECT_NE(_codecCtx->partitions & X264_PART_B8X8, 0);
    EXPECT_EQ(_codecCtx->coder_type, 1);
    EXPECT_NE(_codecCtx->flags & CODEC_FLAG_LOOP_FILTER, 0);
    EXPECT_EQ(_codecCtx->me_subpel_quality, 7);
    EXPECT_EQ(_codecCtx->me_range, 16);
    EXPECT_EQ(_codecCtx->gop_size, 250);
    EXPECT_EQ(_codecCtx->keyint_min, 25);
    EXPECT_EQ(_codecCtx->scenechange_threshold, 40);
    EXPECT_FLOAT_EQ(_codecCtx->i_quant_factor, 0.71f);
    EXPECT_EQ(_codecCtx->b_frame_strategy, 1);
    EXPECT_FLOAT_EQ(_codecCtx->qcompress, 0.6f);
    EXPECT_EQ(_codecCtx->qmin, 10);
    EXPECT_EQ(_codecCtx->qmax, 51);
    EXPECT_EQ(_codecCtx->max_qdiff, 4);
    EXPECT_EQ(_codecCtx->max_b_frames, 3);
    EXPECT_EQ(_codecCtx->refs, 3);
    EXPECT_EQ(_codecCtx->directpred, 1);
    EXPECT_EQ(_codecCtx->trellis, 1);
    EXPECT_EQ(_codecCtx->weighted_p_pred, 2);
    EXPECT_EQ(_codecCtx->cqp, 0);
    EXPECT_EQ(_codecCtx->rc_lookahead, 60);
    EXPECT_EQ(_codecCtx->aq_mode, 0);
}



