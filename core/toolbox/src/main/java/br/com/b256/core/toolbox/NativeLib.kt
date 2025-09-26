package br.com.b256.core.toolbox

class NativeLib {

    /**
     * A native method that is implemented by the 'toolbox' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun compress(input: ByteArray): ByteArray?

    companion object {
        // Used to load the 'toolbox' library on application startup.
        init {
            System.loadLibrary("toolbox")
        }
    }
}
