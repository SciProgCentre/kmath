/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or `null` if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the received value.
 * @return result of the computation of the lambda.
 */
internal inline fun <K, V, R> Map<in K, V>.computeOn(key: K, compute: (V?) -> R): R {
    contract {
        callsInPlace(compute, EXACTLY_ONCE)
    }
    return compute(get(key))
}

/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or computes the given lambda
 * [defaultResult] if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the value corresponding to the [key].
 * @param defaultResult lambda that is computed if the [key] is not present.
 * @return result of [compute] lambda if the [key] is present or result of [defaultResult] otherwise.
 */
internal inline fun <K, V, R> Map<K, V>.computeOnOrElse(key: K, defaultResult: () -> R, compute: (value: V) -> R): R {
    contract {
        callsInPlace(defaultResult, AT_MOST_ONCE)
        callsInPlace(compute, AT_MOST_ONCE)
    }
    @Suppress("UNCHECKED_CAST")
    return (if (key !in this) defaultResult() else compute(get(key) as V))
}

/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or computes the given lambda
 * [defaultResult] if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the value corresponding to the [key].
 * @param defaultResult default result that is returned in case of the [key]'s absence.
 * @return result of [compute] lambda if the [key] is present or [defaultResult] otherwise.
 */
internal inline fun <K, V, R> Map<K, V>.computeOnOrElse(key: K, defaultResult: R, compute: (value: V) -> R): R {
    contract {
        callsInPlace(compute, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, { defaultResult }, compute)
}

/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or computes the given lambda
 * [defaultResult] if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the value corresponding to the [key].
 * @param defaultResult default result that is returned in case of the [key]'s absence.
 * @return result of [compute] lambda if the [key] is present or [defaultResult] otherwise.
 */
internal inline fun <K, V, R> Map<K, V>.computeOnOrElse(key: K, defaultResult: R, compute: (key: K, value: V) -> R): R {
    contract {
        callsInPlace(compute, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, { defaultResult }, { it -> compute(key, it) })
}

/**
 * Applies the [transformation][transform] to the value corresponding to the given [key] or null instead if it's not
 * present.
 *
 * @param key key to check.
 * @param transform transformation to apply.
 * @return result of the transformation
 */
internal inline fun <K, V> MutableMap<in K, V>.applyToKey(key: K, transform: (currentValue: V?) -> V): V {
    contract {
        callsInPlace(transform, EXACTLY_ONCE)
    }
    return computeOn(key, transform).also { this[key] = it }
}

/**
 * Depending on presence of value corresponding to the given [key] either puts new value calculated by [valueOnPut] or
 * changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut lazily calculated value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value as a parameter.
 * @return result value corresponding to the [key].
 */
internal inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): V {
    contract {
        callsInPlace(valueOnPut, AT_MOST_ONCE)
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, valueOnPut, transformOnChange).also { this[key] = it }
}

/**
 * Depending on presence of value corresponding to the given [key] either puts new value [valueOnPut] or
 * changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value as a parameter.
 * @return result value corresponding to the [key].
 */
internal inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: V, transformOnChange: (currentValue: V) -> V): V {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return putOrChange<K, V>(key, { valueOnPut }, transformOnChange)
}

/**
 * Depending on presence of value corresponding to the given [key] either puts new value [valueOnPut] or
 * changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value and new value as parameters.
 * @return result value corresponding to the [key].
 */
internal inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: V, transformOnChange: (currentValue: V, newValue: V) -> V): V {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return putOrChange<K, V>(key, { valueOnPut }, { transformOnChange(it, valueOnPut) })
}

/**
 * Depending on presence of value corresponding to the given [key] either puts new value [valueOnPut] or
 * changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * the [key], current value, and new value as parameters.
 * @return result value corresponding to the [key].
 */
internal inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: V, transformOnChange: (key: K, currentValue: V, newValue: V) -> V): V {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return putOrChange<K, V>(key, { valueOnPut }, { transformOnChange(key, it, valueOnPut) })
}

/**
 * Creates copy of [the map][this] and applies the [transformation][transform] to the value corresponding to the given
 * [key] in the copy or null instead if it's not present.
 *
 * @param key key to check.
 * @param transform transformation to apply.
 * @return the copy of [the map][this].
 */
internal inline fun <K, V> Map<in K, V>.withAppliedToKey(key: K, transform: (currentValue: V?) -> V): Map<K, V> {
    contract {
        callsInPlace(transform, EXACTLY_ONCE)
    }
    return buildMap(size) {
        putAll(this)
        applyToKey(key, transform)
    }
}

/**
 * Creates copy of [the map][this] and depending on presence of value corresponding to the given [key] either puts new
 * value calculated by [valueOnPut] or changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut lazily calculated value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value as a parameter.
 * @return the copy of [the map][this].
 */
internal inline fun <K, V> Map<K, V>.withPutOrChanged(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): Map<K, V> {
    contract {
        callsInPlace(valueOnPut, AT_MOST_ONCE)
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return buildMap(size + 1) {
        putAll(this@withPutOrChanged)
        putOrChange(key, valueOnPut, transformOnChange)
    }
}

/**
 * Creates copy of [the map][this] and depending on presence of value corresponding to the given [key] either puts new
 * value [valueOnPut] or changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value as a parameter.
 * @return the copy of [the map][this].
 */
internal inline fun <K, V> Map<K, V>.withPutOrChanged(key: K, valueOnPut: V, transformOnChange: (currentValue: V) -> V): Map<K, V> {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return withPutOrChanged<K, V>(key, { valueOnPut }, transformOnChange)
}

/**
 * Creates copy of [the map][this] and depending on presence of value corresponding to the given [key] either puts new
 * value [valueOnPut] or changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value and new value as parameters.
 * @return the copy of [the map][this].
 */
internal inline fun <K, V> Map<K, V>.withPutOrChanged(key: K, valueOnPut: V, transformOnChange: (currentValue: V, newValue: V) -> V): Map<K, V> {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return withPutOrChanged<K, V>(key, { valueOnPut }, { transformOnChange(it, valueOnPut) })
}

/**
 * Creates copy of [the map][this] and depending on presence of value corresponding to the given [key] either puts new
 * value [valueOnPut] or changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * the [key], current value, and new value as parameters.
 * @return the copy of [the map][this].
 */
internal inline fun <K, V> Map<K, V>.withPutOrChanged(key: K, valueOnPut: V, transformOnChange: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return withPutOrChanged<K, V>(key, { valueOnPut }, { transformOnChange(key, it, valueOnPut) })
}

/**
 * Copies entries of [this map][this] to the [destination] map overriding present ones if needed.
 *
 * @receiver map to be copied.
 * @param destination map to receive copies.
 * @return the [destination].
 */
internal fun <K, V, D: MutableMap<K, V>> Map<K, V>.copyTo(destination: D): D {
    for ((key, value) in this) {
        destination[key] = value
    }
    return destination
}

/**
 * Copies entries of [this map][this] to the [destination] map merging present entries with new ones using [resolve]
 * lambda.
 *
 * @receiver map to be copied.
 * @param destination map to receive copies.
 * @param resolve lambda function that resolves overriding. It takes a key, current value corresponding to the key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
internal inline fun <K, V: W, W, D: MutableMap<K, W>> Map<out K, V>.copyToBy(destination: D, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for ((key, value) in this) {
        destination.putOrChange(key, value) { it -> resolve(key, it, value) }
    }
    return destination
}

/**
 * Copies entries of [this map][this] to the [destination] map merging present entries with new ones using [resolve]
 * lambda.
 *
 * @receiver map to be copied.
 * @param destination map to receive copies.
 * @param resolve lambda function that resolves overriding. It takes current value corresponding to some key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
internal inline fun <K, V: W, W, D: MutableMap<K, W>> Map<out K, V>.copyToBy(destination: D, resolve: (currentValue: W, newValue: V) -> W): D =
    copyToBy(destination) { _, currentValue, newValue -> resolve(currentValue, newValue) }

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map overriding present ones if needed. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyTo(destination)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @return the [destination].
 */
internal inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapTo(destination: D, transform: (Map.Entry<K, V>) -> W): D {
    for (entry in this) {
        destination[entry.key] = transform(entry)
    }
    return destination
}

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map overriding present ones if needed. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyTo(destination)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @return the [destination].
 */
internal inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapTo(destination: D, transform: (key: K, value: V) -> W): D =
    copyMapTo(destination) { (key, value) -> transform(key, value) }

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map merging present entries with new ones using [resolve] lambda. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyToBy(destination, resolve)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @param resolve lambda function that resolves overriding. It takes a key, current value corresponding to the key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
internal inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for (entry in this) {
        val (key, value) = entry
        destination.putOrChange(key, transform(entry)) { it -> resolve(key, it, value) }
    }
    return destination
}

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map merging present entries with new ones using [resolve] lambda. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyToBy(destination, resolve)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @param resolve lambda function that resolves overriding. It takes a key, current value corresponding to the key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
internal inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (key: K, value: V) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D =
    copyMapToBy(destination, { (key, value) -> transform(key, value) }, resolve)

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map merging present entries with new ones using [resolve] lambda. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyToBy(destination, resolve)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @param resolve lambda function that resolves overriding. It takes current value corresponding to some key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
internal inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (currentValue: W, newValue: V) -> W): D =
    copyMapToBy(destination, transform, { _, currentValue, newValue -> resolve(currentValue, newValue) })

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map merging present entries with new ones using [resolve] lambda. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyToBy(destination, resolve)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @param resolve lambda function that resolves overriding. It takes current value corresponding to some key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
internal inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (key: K, value: V) -> W, resolve: (currentValue: W, newValue: V) -> W): D =
    copyMapToBy(destination, { (key, value) -> transform(key, value) }, { _, currentValue, newValue -> resolve(currentValue, newValue) })

/**
 * Merges [the first map][map1] and [the second map][map2] prioritising the second one, puts result to the [destination]
 * and returns the [destination].
 *
 * Precisely, corresponding keys and values of the received maps are put into the destination overriding existing values
 * in the [destination] if needed. For every key appearing in both maps corresponding value from the second map is
 * chosen.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @param destination the map where result of the merge is put.
 * @return the destination.
 */
internal fun <K, V, D: MutableMap<in K, in V>> mergeTo(map1: Map<out K, V>, map2: Map<out K, V>, destination: D): D {
    for ((key, value) in map1) {
        destination.put(key, value)
    }
    for ((key, value) in map2) {
        destination.put(key, value)
    }
    return destination
}

/**
 * Merges [the first map][map1] and [the second map][map2] resolving conflicts with [resolve] lambda, puts result to the
 * [destination] and returns the [destination].
 *
 * Precisely, corresponding keys and values of the received maps are put into the destination overriding existing values
 * in the [destination] if needed. For every key appearing in both maps corresponding value is a result of the [resolve]
 * lambda calculated on the key and its corresponding values from the merged maps.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @param resolve lambda function that resolves merge conflicts.
 * @param destination the map where result of the merge is put.
 * @return the destination.
 */
internal inline fun <K, V1: W, V2: W, W, D: MutableMap<K, W>> mergeToBy(map1: Map<out K, V1>, map2: Map<out K, V2>, destination: D, resolve: (key: K, value1: V1, value2: V2) -> W): D {
    for (key in map2.keys) {
        destination.remove(key)
    }
    for ((key, value) in map1) {
        destination.put(key, value)
    }
    for ((key, value) in map2) {
        @Suppress("UNCHECKED_CAST")
        destination.putOrChange(key, value) { it -> resolve(key, it as V1, value) }
    }
    return destination
}

/**
 * Merges [the first map][map1] and [the second map][map2] resolving conflicts with [resolve] lambda, puts result to the
 * [destination] and returns the [destination].
 *
 * Precisely, corresponding keys and values of the received maps are put into the destination overriding existing values
 * in the [destination] if needed. For every key appearing in both maps corresponding value is a result of the [resolve]
 * lambda calculated on the key's corresponding values from the merged maps.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @param resolve lambda function that resolves merge conflicts.
 * @param destination the map where result of the merge is put.
 * @return the destination.
 */
internal inline fun <K, V1: W, V2: W, W, D: MutableMap<K, W>> mergeToBy(map1: Map<K, V1>, map2: Map<K, V2>, destination: D, resolve: (value1: V1, value2: V2) -> W): D =
    mergeToBy(map1, map2, destination) { _, value1, value2 -> resolve(value1, value2) }

/**
 * Merges [the first map][map1] and [the second map][map2] prioritising the second one.
 *
 * Precisely, corresponding keys and values of the received maps are put into a new empty map which is returned after
 * afterwards. For every key appearing in both maps corresponding value from the second map is chosen.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @return the result of the merge.
 */
internal fun <K, V1: W, V2: W, W> merge(map1: Map<K, V1>, map2: Map<K, V2>): Map<K, W> {
    val result = LinkedHashMap<K, W>(map1.size + map2.size)
    return mergeTo(map1, map2, result)
}

/**
 * Merges [the first map][map1] and [the second map][map2] resolving conflicts with [resolve] lambda.
 *
 * Precisely, corresponding keys and values of the received maps are put into a new empty map which is returned after
 * afterwards. For every key appearing in both maps corresponding value is a result of the [resolve] lambda calculated
 * on the key and its corresponding values from the merged maps.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @param resolve lambda function that resolves merge conflicts.
 * @return the result of the merge.
 */
internal inline fun <K, V1: W, V2: W, W> mergeBy(map1: Map<K, V1>, map2: Map<K, V2>, resolve: (key: K, value1: V1, value2: V2) -> W): Map<K, W> {
    val result = LinkedHashMap<K, W>(map1.size + map2.size)
    return mergeToBy(map1, map2, result, resolve)
}

/**
 * Merges [the first map][map1] and [the second map][map2] resolving conflicts with [resolve] lambda.
 *
 * Precisely, corresponding keys and values of the received maps are put into a new empty map which is returned after
 * afterwards. For every key appearing in both maps corresponding value is a result of the [resolve] lambda calculated
 * on the key's corresponding values from the merged maps.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @param resolve lambda function that resolves merge conflicts.
 * @return the result of the merge.
 */
internal inline fun <K, V1: W, V2: W, W> mergeBy(map1: Map<K, V1>, map2: Map<K, V2>, resolve: (value1: V1, value2: V2) -> W): Map<K, W> =
    mergeBy(map1, map2) { _, value1, value2 -> resolve(value1, value2) }

/**
 * Populates the [destination] map with key-value pairs provided by [transform] function applied to each element of the
 * given collection resolving conflicts with [resolve] function and returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each element to key-value.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateTo(destination: D, transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val (key, value) = transform(element)
        destination.putOrChange(key, value, resolve)
    }
    return destination
}

/**
 * Populates the [destination] map with key-value pairs, where key is provided by [keySelector] function and value is
 * provided by [valueTransform] applied to each element of the given collection, resolving conflicts with [resolve]
 * function and returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param valueTransform lambda functions that generates value for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val value = valueTransform(element)
        destination.putOrChange(key, value, resolve)
    }
    return destination
}

/**
 * Populates the [destination] map with key-value pairs, where key is provided by [keySelector] function applied to each
 * element of the given collection and value is the element itself, resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <T, K, D : MutableMap<K, T>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): D {
    for (element in this) {
        val key = keySelector(element)
        destination.putOrChange(key, element, resolve)
    }
    return destination
}

/**
 * Populates the [destination] map with key-value pairs provided by [transform] function applied to each element of the
 * given collection resolving conflicts with [resolve] function and returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each element to key-value pair.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateTo(destination: D, transform: (T) -> Pair<K, V>, resolve: (currentValue: V, newValue: V) -> V): D =
    associateTo(destination, transform) { _, currentValue, newValue -> resolve(currentValue, newValue) }

/**
 * Populates the [destination] map with key-value pairs, where key is provided by [keySelector] function and value is
 * provided by [valueTransform] applied to each element of the given collection, resolving conflicts with [resolve]
 * function and returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param valueTransform lambda functions that generates value for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (currentValue: V, newValue: V) -> V): D =
    associateByTo(destination, keySelector, valueTransform) { _, currentValue, newValue -> resolve(currentValue, newValue) }

/**
 * Populates the [destination] map with key-value pairs, where key is provided by [keySelector] function applied to each
 * element of the given collection and value is the element itself, resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <T, K, D : MutableMap<K, T>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, resolve: (currentValue: T, newValue: T) -> T): D =
    associateByTo(destination, keySelector) { _, currentValue, newValue -> resolve(currentValue, newValue) }

/**
 * Returns a map containing key-value pairs provided by [transform] function applied to elements of the given collection.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with the
 * key is resolved with [resolve] function which takes the key, current value corresponding to the key, and new value
 * from the pair.
 *
 * @param transform function which transforms each element to key-value pair.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
    associateTo(LinkedHashMap(), transform, resolve)

/**
 * Returns a map containing the values provided by [valueTransform] and indexed by [keySelector] functions applied to
 * elements of the given collection.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes the key, current value corresponding to the key, and new
 * value from the pair.
 *
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param valueTransform lambda functions that generates value for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <T, K, V> Iterable<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
    associateByTo(LinkedHashMap(), keySelector, valueTransform, resolve)

/**
 * Returns a map containing the elements from the given collection indexed by the key returned from [keySelector]
 * function applied to each element.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes the key, current value corresponding to the key, and new
 * value from the pair.
 *
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): Map<K, T> =
    associateByTo(LinkedHashMap(), keySelector, resolve)

/**
 * Returns a map containing key-value pairs provided by [transform] function applied to elements of the given collection.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes current value corresponding to the key and new value from the
 * pair.
 *
 * @param transform function which transforms each element to key-value pair.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>, resolve: (currentValue: V, newValue: V) -> V): Map<K, V> =
    associateTo(LinkedHashMap(), transform, resolve)

/**
 * Returns a map containing the values provided by [valueTransform] and indexed by [keySelector] functions applied to
 * elements of the given collection.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes current value corresponding to the key and new value from the
 * pair.
 *
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param valueTransform lambda functions that generates value for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <T, K, V> Iterable<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (currentValue: V, newValue: V) -> V): Map<K, V> =
    associateByTo(LinkedHashMap(), keySelector, valueTransform, resolve)

/**
 * Returns a map containing the elements from the given collection indexed by the key returned from [keySelector]
 * function applied to each element.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes current value corresponding to the key and new value from the
 * pair.
 *
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K, resolve: (currentValue: T, newValue: T) -> T): Map<K, T> =
    associateByTo(LinkedHashMap(), keySelector, resolve)

/**
 * Populates the given [destination] map with entries having the keys of this map and the values obtained
 * by applying the [transform] function to each entry in this map resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new value.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <K, V, W, D : MutableMap<K, W>> Map<out K, V>.mapValuesTo(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: W) -> W): D =
    entries.associateByTo(destination, { it.key }, transform, resolve)

/**
 * Populates the given [destination] map with entries having the keys of this map and the values obtained
 * by applying the [transform] function to each entry in this map resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new value.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <K, V, W, D : MutableMap<K, W>> Map<out K, V>.mapValuesTo(destination: D, transform: (key: K, value: V) -> W, resolve: (key: K, currentValue: W, newValue: W) -> W): D =
    entries.associateByTo(destination, { it.key }, { (key, value) -> transform(key, value) }, resolve)

/**
 * Populates the given [destination] map with entries having the keys of this map and the values obtained
 * by applying the [transform] function to each entry in this map resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new value.
 * @param resolve lambda function that resolves merge conflicts which current and new values corresponding to some key.
 * @return the [destination].
 */
internal inline fun <K, V, W, D : MutableMap<K, W>> Map<out K, V>.mapValuesTo(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (currentValue: W, newValue: W) -> W): D =
    entries.associateByTo(destination, { it.key }, transform, resolve)

/**
 * Populates the given [destination] map with entries having the keys of this map and the values obtained
 * by applying the [transform] function to each entry in this map resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new value.
 * @param resolve lambda function that resolves merge conflicts which current and new values corresponding to some key.
 * @return the [destination].
 */
internal inline fun <K, V, W, D : MutableMap<K, W>> Map<out K, V>.mapValuesTo(destination: D, transform: (key: K, value: V) -> W, resolve: (currentValue: W, newValue: W) -> W): D =
    entries.associateByTo(destination, { it.key }, { (key, value) -> transform(key, value) }, resolve)

/**
 * Populates the given [destination] map with entries having the keys obtained by applying the [transform] function to
 * each entry in this map and the values of this map, resolving conflicts with [resolve] function and returns the
 * [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <K, V, L, D : MutableMap<L, V>> Map<out K, V>.mapKeysTo(destination: D, transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): D =
    entries.associateByTo(destination, transform, { it.value }, resolve)

/**
 * Populates the given [destination] map with entries having the keys obtained by applying the [transform] function to
 * each entry in this map and the values of this map, resolving conflicts with [resolve] function and returns the
 * [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
internal inline fun <K, V, L, D : MutableMap<L, V>> Map<out K, V>.mapKeysTo(destination: D, transform: (key: K, value: V) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): D =
    entries.associateByTo(destination, { (key, value) -> transform(key, value) }, { it.value }, resolve)

/**
 * Populates the given [destination] map with entries having the keys obtained by applying the [transform] function to
 * each entry in this map and the values of this map, resolving conflicts with [resolve] function and returns the
 * [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which current and new values corresponding to some key.
 * @return the [destination].
 */
internal inline fun <K, V, L, D : MutableMap<L, V>> Map<out K, V>.mapKeysTo(destination: D, transform: (Map.Entry<K, V>) -> L, resolve: (currentValue: V, newValue: V) -> V): D =
    entries.associateByTo(destination, transform, { it.value }, resolve)

/**
 * Populates the given [destination] map with entries having the keys obtained by applying the [transform] function to
 * each entry in this map and the values of this map, resolving conflicts with [resolve] function and returns the
 * [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which current and new values corresponding to some key.
 * @return the [destination].
 */
internal inline fun <K, V, L, D : MutableMap<L, V>> Map<out K, V>.mapKeysTo(destination: D, transform: (key: K, value: V) -> L, resolve: (currentValue: V, newValue: V) -> V): D =
    entries.associateByTo(destination, { (key, value) -> transform(key, value) }, { it.value }, resolve)

/**
 * Returns a new map with entries having the keys obtained by applying the [transform] function to each entry in this
 * map and the values of this map and resolving conflicts with [resolve] function.
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <K, V, L> Map<out K, V>.mapKeys(transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): Map<L, V> =
    mapKeysTo(LinkedHashMap(size), transform, resolve)

/**
 * Returns a new map with entries having the keys obtained by applying the [transform] function to each entry in this
 * map and the values of this map and resolving conflicts with [resolve] function.
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
internal inline fun <K, V, L> Map<out K, V>.mapKeys(transform: (key: K, value: V) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): Map<L, V> =
    mapKeysTo(LinkedHashMap(size), transform, resolve)

/**
 * Returns a new map with entries having the keys obtained by applying the [transform] function to each entry in this
 * map and the values of this map and resolving conflicts with [resolve] function.
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which current and new values corresponding to some key.
 * @return the result map.
 */
internal inline fun <K, V, L> Map<out K, V>.mapKeys(transform: (Map.Entry<K, V>) -> L, resolve: (currentValue: V, newValue: V) -> V): Map<L, V> =
    mapKeysTo(LinkedHashMap(size), transform, resolve)

/**
 * Returns a new map with entries having the keys obtained by applying the [transform] function to each entry in this
 * map and the values of this map and resolving conflicts with [resolve] function.
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which current and new values corresponding to some key.
 * @return the result map.
 */
internal inline fun <K, V, L> Map<out K, V>.mapKeys(transform: (key: K, value: V) -> L, resolve: (currentValue: V, newValue: V) -> V): Map<L, V> =
    mapKeysTo(LinkedHashMap(size), transform, resolve)