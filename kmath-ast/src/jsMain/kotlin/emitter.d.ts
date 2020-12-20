export class Emitter {
    constructor(obj: any)
    constructor()

    on(event: string, fn: () => void)

    off(event: string, fn: () => void)

    once(event: string, fn: () => void)

    emit(event: string, ...any: any[])

    listeners(event: string): (() => void)[]

    hasListeners(event: string): boolean
}
