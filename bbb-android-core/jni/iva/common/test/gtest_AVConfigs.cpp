#include <error.h>
#include "gtest_AVConfigs.h"

TEST_F(AVConfigsTest, CheckResolutions)
{
    // verifica se tem pelo menos uma resolução cadastrada
    const list<pair<int,int>> resolutions = _configs.getResolutions();
    
    EXPECT_GE(resolutions.size(), 1) << "Deve existir pelo menos uma resolução cadastrada";
    
    // todas resoluções devem ser válidas
    list<pair<int,int>>::const_iterator it;
    for (it = resolutions.begin(); it != resolutions.end(); ++it) {
        EXPECT_GT((*it).first, 0) << "Largura da resolução não é válida";
        EXPECT_GT((*it).second, 0) << "Altura da resolução não é válida";
    }

    // tenta validar algumas resoluções
    EXPECT_TRUE(_configs.validateResolution(resolutions.front()));
    EXPECT_FALSE(_configs.validateResolution(make_pair(0,0)));
    EXPECT_FALSE(_configs.validateResolution(make_pair(-10,-10)));

    stringstream ss;
    ss << resolutions.front().first << "x" << resolutions.front().second;
    EXPECT_TRUE(_configs.validateResolution(ss.str()));
    EXPECT_FALSE(_configs.validateResolution("0x0"));
    EXPECT_FALSE(_configs.validateResolution("-10x-10"));
}

TEST_F(AVConfigsTest, CheckFps)
{
    // valida o range
    pair<int,int> fps = _configs.getFpsRange();
    EXPECT_GE(fps.first, 1) << "Fps mínimo deve ser pelo menos 1";
    EXPECT_GE(fps.second, 5) << "Fps máximo deve ser pelo menos 5";
    EXPECT_LE(fps.second, 90) << "Fps máximo deve ser menor que 90";
    
    // verifica a validação
    EXPECT_TRUE(_configs.validateFps(fps.second/2));
    EXPECT_TRUE(_configs.validateFps(fps.first));
    EXPECT_TRUE(_configs.validateFps(fps.second));
    EXPECT_FALSE(_configs.validateFps(fps.second+1));
    EXPECT_FALSE(_configs.validateFps(fps.first-1));
}

TEST_F(AVConfigsTest, CheckAudioBitrate)
{
    // verifica se tem pelo menos um bitrate
    const list<int> bitrates = _configs.getAudioBitrates();
    
    EXPECT_GE(bitrates.size(), 1) << "Deve existir pelo menos um bitrate";
    
    // todos bitrates devem ser válidos
    for (list<int>::const_iterator it = bitrates.begin(); it != bitrates.end(); ++it) {
        EXPECT_GT(*it, 0) << "Bitrate deve ser > 0";
        EXPECT_GT(*it, 0) << "Bitrate deve ser < 512";
    }

    // tenta validar algunss bitrates
    EXPECT_TRUE(_configs.validateAudioBitrate(bitrates.front()));
    EXPECT_FALSE(_configs.validateAudioBitrate(0));
    EXPECT_FALSE(_configs.validateAudioBitrate(-10));
    EXPECT_FALSE(_configs.validateAudioBitrate(9999));
}

TEST_F(AVConfigsTest, CheckVideoBitrate)
{
    const list<pair<int,int>> resolutions = _configs.getResolutions();

    for (list<pair<int,int>>::const_iterator it = resolutions.begin();
         it != resolutions.end(); ++it) {

        pair<int,int> rates = _configs.getVideoBitrateRange(*it);
        EXPECT_GE(rates.second, rates.first) << "Range de bitrates inválido";
        EXPECT_GE(rates.first, 50) << "Bitrates devem ser no mínimo 50 kbit/s";
        EXPECT_LE(rates.first, 10000) << "Bitrates devem ser no máximo 10 Mbit/s";

        EXPECT_TRUE(_configs.validateVideoBitrate(*it, rates.first));
        EXPECT_TRUE(_configs.validateVideoBitrate(*it, rates.second));
        EXPECT_TRUE(_configs.validateVideoBitrate(*it,
            rates.second/2 > rates.first ? rates.second/2 : rates.first
            ));
        EXPECT_FALSE(_configs.validateVideoBitrate(*it, 0));
        EXPECT_FALSE(_configs.validateVideoBitrate(*it, rates.first-1));
        EXPECT_FALSE(_configs.validateVideoBitrate(*it, rates.second+1));
    }
}

TEST_F(AVConfigsTest, CheckVideoAndAudioCodecs)
{
    // verifica se tem pelo menos um codec
    const list<int> &acodecs = _configs.getAudioCodecs();
    const list<int> &vcodecs = _configs.getVideoCodecs();
    EXPECT_GE(acodecs.size(), 1) << "Deve existir pelo menos um codec de áudio";
    EXPECT_GE(vcodecs.size(), 1) << "Deve existir pelo menos um codec de vídeo";
    
    // não pode haver um codec nas duas listas ao mesmo tempo
    list<int>::const_iterator ait;
    list<int>::const_iterator vit;
    for (ait = acodecs.begin(); ait != acodecs.end(); ++ait) {
        for (vit = vcodecs.begin(); vit != vcodecs.end(); ++vit) {
            EXPECT_FALSE(*ait == *vit) << "Há um codec na lista de áudio e vídeo ao mesmo tempo";
        }
    }

    // tenta validar algumas resoluções
    EXPECT_TRUE(_configs.validateAudioCodec(acodecs.front()));
    EXPECT_TRUE(_configs.validateVideoCodec(vcodecs.front()));
    // agora invertidos...
    EXPECT_FALSE(_configs.validateVideoCodec(acodecs.front()));
    EXPECT_FALSE(_configs.validateAudioCodec(vcodecs.front()));
}

