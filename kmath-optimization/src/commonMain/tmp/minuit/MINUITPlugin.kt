/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */
package space.kscience.kmath.optimization.minuit

import hep.dataforge.context.*

/**
 * Мэнеджер для MINUITа. Пока не играет никакой активной роли кроме ведения
 * внутреннего лога.
 *
 * @author Darksnake
 * @version $Id: $Id
 */
@PluginDef(group = "hep.dataforge",
    name = "MINUIT",
    dependsOn = ["hep.dataforge:fitting"],
    info = "The MINUIT fitter engine for DataForge fitting")
class MINUITPlugin : BasicPlugin() {
    fun attach(@NotNull context: Context?) {
        super.attach(context)
        clearStaticLog()
    }

    @Provides(Fitter.FITTER_TARGET)
    fun getFitter(fitterName: String): Fitter? {
        return if (fitterName == "MINUIT") {
            MINUITFitter()
        } else {
            null
        }
    }

    @ProvidesNames(Fitter.FITTER_TARGET)
    fun listFitters(): List<String> {
        return listOf("MINUIT")
    }

    fun detach() {
        clearStaticLog()
        super.detach()
    }

    class Factory : PluginFactory() {
        fun build(meta: Meta?): Plugin {
            return MINUITPlugin()
        }

        fun getType(): java.lang.Class<out Plugin?> {
            return MINUITPlugin::class.java
        }
    }

    companion object {
        /**
         * Constant `staticLog`
         */
        private val staticLog: Chronicle? = Chronicle("MINUIT-STATIC", Global.INSTANCE.getHistory())

        /**
         *
         *
         * clearStaticLog.
         */
        fun clearStaticLog() {
            staticLog.clear()
        }

        /**
         *
         *
         * logStatic.
         *
         * @param str  a [String] object.
         * @param pars a [Object] object.
         */
        fun logStatic(str: String?, vararg pars: Any?) {
            checkNotNull(staticLog) { "MINUIT log is not initialized." }
            staticLog.report(str, pars)
            LoggerFactory.getLogger("MINUIT").info(String.format(str, *pars))
            //        Out.out.printf(str,pars);
//        Out.out.println();
        }
    }
}