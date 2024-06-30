package com.varabyte.kobwebx.chrome.ai

/**
 * Records the availability of model execution as returned in methods like [AIManager.canCreateGenericSession] and [AIManager.canCreateTextSession]
 */
@Suppress("unused") // Used for checking Test Session is available or not
enum class AIModelAvailability {
    /**
     * The model is available on-device and so creating will happen quickly.
     */
    READILY,

    /**
     * The model is not available on-device, but the device is capable, so creating
     * the model will start the download process (which can take a while).
     */
    AFTER_DOWNLOAD,

    /**
     * The model is not available for this device.
     */
    NO
}

