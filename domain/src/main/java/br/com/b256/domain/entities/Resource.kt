package br.com.b256.domain.entities

/**
 * Tipo padrão do template para representar o resultado de uma operação assíncrona (ex.: uma
 * chamada de rede ou leitura de banco) que pode estar em andamento, ter sucesso ou falhar.
 *
 * Uso recomendado: expor `Flow<Resource<T>>`/`suspend fun ...: Resource<T>` em vez de lançar
 * exceção diretamente, e tratar os três casos com `when` no consumidor (ex.: um `ViewModel`
 * mapeando para o estado de UI).
 */
sealed interface Resource<out T> {
    /** Operação concluída com sucesso, com o [data] resultante. */
    data class Success<T>(val data: T) : Resource<T>

    /** Operação falhou; [exception] carrega a causa original. */
    data class Failure(val exception: Throwable) : Resource<Nothing>

    /** Operação ainda em andamento. */
    data object Loading : Resource<Nothing>
}
