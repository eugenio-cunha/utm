package br.com.b256.core.common.extension

/**
 * Adiciona ou atualiza um elemento da lista
 *
 * @param element Elemento a ser adicionado ou atualizado
 *
 * @return Retorna uma lista de elementos da coleção original e o [element] atualizado ou adicionado.
 */
fun <T> Collection<T>.upsert(element: T): List<T> {
    var isUpdated = false
    var result = listOf<T>()

    for (item in this) {
        result = if (item?.equals(element) == true) {
            // Defini que ocorreu uma atualização na lista
            isUpdated = true

            // Adiciona o elemento a nova lista para substituir o elemento desatualizado
            result.plus(element = element)
        } else {
            // Adiciona o item a nova lista
            result.plus(element = item)
        }
    }

    // Caso nenhum elemento da lista seja atualizado, adiciona ao fim o novo elemento
    if (!isUpdated) {
        result = result.plus(element = element)
    }

    // Retorna a nova lista de elementos
    return result
}

/**
 * Adiciona ou atualiza uma lista de elementos
 *
 * @param element Lista de elemento a ser adicionado ou atualizado
 *
 * @return Retorna uma lista de elementos da coleção original e o [element] atualizado ou adicionado.
 */
fun <T> Collection<T>.upsert(element: List<T>): List<T> {
    var result = this

    for (item in element) {
        result = result.upsert(item)
    }

    // Retorna a nova lista de elementos
    return result.toList()
}

/**
 * Remove um elemento da lista caso o mesmo exista
 *
 * @param element Elemento a ser removido
 *
 * @return Retorna uma lista de elementos da coleção menos o [element] removido.
 */
fun <T> Collection<T>.delete(element: T): List<T> {
    return this.filter { it?.equals(element) == false }
}

/**
 * Remove uma lista de elementos caso o mesmo exista
 *
 * @param element Lista de elemento a ser removido
 *
 * @return Retorna uma lista de elementos da coleção menos os [element] removidos.
 */
fun <T> Collection<T>.delete(element: List<T>): List<T> {
    var result = this

    for (item in element) {
        result = result.delete(item)
    }

    return result.toList()
}
