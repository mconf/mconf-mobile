#ifndef _IPV4_H
#define _IPV4_H

#include "IvaString.h"
#include "errorDefs.h"

/**
 * \brief Abstrai o conceito de IP (Internet Protocol).
 */
class IPv4 {

public:
    enum IPv4Type {
        TYPE_UNDEFINED,
        TYPE_UNICAST,
        TYPE_MULTICAST,
        TYPE_BROADCAST
    };

    IPv4() throw (int);

	/**
	 * \brief Construtor.
	 *
	 * Inicializa o IP com base nos valores dos quatro bytes.
	 *
	 * \param byte1 Primeiro byte do IP.
	 * \param byte2 Segundo byte do IP.
	 * \param byte3 Terceiro byte do IP.
	 * \param byte4 Quarto byte do IP.
	 */
	IPv4(int byte1, int byte2, int byte3, int byte4) throw (int);

	/**
	 * \brief Construtor.
	 *
	 * Inicializa o IP com base em uma string de IP.
	 *
	 * \param in String de IP no modelo "XXX.YYY.ZZZ.WWW".
	 */
	IPv4(const IvaString& in) throw (int);

	/**
	 * \brief Construtor.
	 *
	 * Inicializa o IP com base em um inteiro, sendo que cada byte do inteiro
	 * representa um dos quatro campos do IP.
	 *
	 * \param in Inteiro com o valor do IP.
	 */
	IPv4(unsigned int in);

	IPv4(const IPv4 & ent);

	/**
	 * \brief Destrutor.
	 */
	~IPv4();

	/**
	 * \brief Retorna o IP no formato de string.
	 *
	 * \param out[out] Parâmetro de saída com a string no formato
	 * "XXX.YYY.ZZZ.WWW"
	 */
	void getString(IvaString & out) const;

    IvaString getString() const;

    int setString(const IvaString& in);

	/**
	 * \brief Retorna o IP no formato de inteiro.
	 *
	 * \return IP no formato de inteiro com os bytes
	 * representando cada campo do IP.
	 */
	unsigned int getInteger() const;

    void setInt(unsigned int in);

	/**
	 * \brief Retorna o IP no formato de quatro bytes.
	 *
	 * \param byte1 Primeiro byte do IP.
	 * \param byte2 Segundo byte do IP.
	 * \param byte3 Terceiro byte do IP.
	 * \param byte4 Quarto byte do IP.
	 */
	void getBytes(int * byte1, int * byte2, int * byte3, int * byte4);

    IPv4::IPv4Type getType() const;


	IPv4 & operator=(const IPv4 & operand);

    bool operator>(const IPv4& operand) const;
    bool operator>=(const IPv4& operand) const;
    bool operator==(const IPv4& operand) const;
    bool operator<=(const IPv4& operand) const;
    bool operator<(const IPv4& operand) const;

 private:

	vector<int> bytes; ///< \brief Armazena os quatro bytes do IP.

};

#endif
