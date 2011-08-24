/*
 * Revisão do último commit desta lib/entidade: 1293
 * Data do último commit desta lib/entidade: 2009/10/08 14:34:20
 * Range do último commit: 1318
 * URL: https://143.54.132.132/svn/iva/trunk/common
 * Data de geração deste arquivo: 2009/10/16 01:32:38
 */
 
#ifndef _SW_VERSION_H_
#define _SW_VERSION_H_

// numeração das versões segue o padrão: major.compatibility.minor.revision
// Major: versão maior do sofware
// Compatibility: indica a compatibilidade entre as versões (todas versões X.Y são compatíveis entre si)
// Minor: versão para correções menores que não  influenciam a compatibilidade
// Revision: última revisão do svn

#define SW_VERSION_MAJOR        1                    ///< Versão geral
#define SW_VERSION_COMPAT       2                    ///< Versão compatibilidade
#define SW_VERSION_MINOR        17                   ///< Versão secundária
#define SW_VERSION_REV          1293              ///< Versão SVN
#define SW_VERSION_TYPE         dev                  ///< Tipo do release: "dev" quando versão em desenvolvimento
#define SW_VERSION_STR_4        "1.2.17.1293b-dev"  ///< String com nome inteiro da versão
#define SW_VERSION_STR_3        "1.2.17b"             ///< String com nome da versão simplificada

#endif // _SW_VERSION_H_

