#ifndef _DIRECTORY_H
#define _DIRECTORY_H

#include "IvaString.h"

/**
 * \brief Abstração do diretório que o Sistema Operacional provê
 */
class Directory
{

 public:

	/**
	 * \brief Cria a abstração do diretório de localização \a name
	 *
	 * Associa um diretório físico à classe Directory, criando o diretório
	 * físico, caso necessário.
	 *
	 * \param name Localização do diretório.
	 */
	Directory(IvaString name);

	/**
	 * Destrutor
	 */
	~Directory();

	/**
	 * \brief Verifica se o diretório existe.
	 *
	 * \return Se o diretório existe.
	 * \retval true Diretório existe.
	 * \retval false Diretório não existe.
	 */
	bool exists();

	/**
	 * \brief Deleta o diretório físico correspondente ao objeto.
	 *
	 * \return Se o diretório foi deletado.
	 * \retval false Não foi possível deletar o diretório.
	 * \retval true Diretório deletado.
	 */
	bool remove();

	/**
	 * \brief Cria o diretório físico correspondente ao objeto Directory.
	 *
	 * \return Se o diretório foi criado.
	 * \retval false Não foi possível criar o diretório.
	 * \retval true Diretório criado.
	 */
	bool create();

private:

	IvaString name_; ///< Localização do Diretório físico.

};

#endif
