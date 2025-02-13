package com.truecaller

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.BaseActivityEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.truecaller.android.sdk.oAuth.CodeVerifierUtil
import com.truecaller.android.sdk.oAuth.TcOAuthCallback
import com.truecaller.android.sdk.oAuth.TcOAuthData
import com.truecaller.android.sdk.oAuth.TcOAuthError
import com.truecaller.android.sdk.oAuth.TcSdk
import com.truecaller.android.sdk.oAuth.TcSdkOptions
import java.math.BigInteger
import java.security.SecureRandom

@ReactModule(name = TruecallerModule.NAME)
class TruecallerModule(reactContext: ReactApplicationContext) :
  NativeTruecallerSpec(reactContext) {
  private var codeVerifier: String? = null
  private var promise: Promise? = null

  override fun getName(): String {
    return NAME
  }

  private val activityEventListener = object : BaseActivityEventListener() {
    override fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(activity, requestCode, resultCode, data)
      if (TcSdk.getInstance().isOAuthFlowUsable) {
        TcSdk.getInstance().onActivityResultObtained(activity as FragmentActivity, requestCode, resultCode, data)
      }
    }
  }

  init {
    reactContext.addActivityEventListener(activityEventListener)
  }

  override fun initSDK(promise: Promise?) {
    Log.d(TAG, "initSDK")
    val tcSdkOptions = TcSdkOptions.Builder(reactApplicationContext, tcOAuthCallback)
//      .buttonColor(Color.parseColor("<<VALID_COLOR_HEX_CODE>>"))
//      .buttonTextColor(Color.parseColor("<<VALID_COLOR_HEX_CODE>>"))
      .loginTextPrefix(TcSdkOptions.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
      .ctaText(TcSdkOptions.CTA_TEXT_CONTINUE)
      .buttonShapeOptions(TcSdkOptions.BUTTON_SHAPE_ROUNDED)
      .footerType(TcSdkOptions.FOOTER_TYPE_SKIP)
//      .consentTitleOption(TcSdkOptions.SDK_CONSENT_TITLE_LOG_IN)
      .sdkOptions(TcSdkOptions.OPTION_VERIFY_ONLY_TC_USERS)
      .build();
    TcSdk.init(tcSdkOptions)

    promise?.resolve(true)
  }

  override fun authenticate(promise: Promise?) {
    this.promise = promise
    if (TcSdk.getInstance().isOAuthFlowUsable()) {
      val stateRequestedStr = BigInteger(130, SecureRandom()).toString(32)
      TcSdk.getInstance().setOAuthState(stateRequestedStr)
      TcSdk.getInstance().setOAuthScopes(arrayOf("profile", "phone"))
      codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()
      val codeChallenge = CodeVerifierUtil.getCodeChallenge(codeVerifier!!)

      if (codeChallenge != null) {
        TcSdk.getInstance().setCodeChallenge(codeChallenge)
      } else {
        promise?.reject("ERROR", "Code challenge is null. Cannot proceed further.")
        return
      }
      try {
        TcSdk.getInstance().getAuthorizationCode(reactApplicationContext.currentActivity as FragmentActivity)
      } catch (e: Exception) {
        promise?.reject("ERROR", e.message)
      }
    } else {
      val map = Arguments.createMap().apply {
        putString("error", "ERROR_TYPE_NOT_SUPPORTED")
      }
      promise?.resolve(map)
    }
  }

  override fun isTrueCallerAppInstall(promise: Promise?) {
    Log.d(TAG, "isTrueCallerAppInstall")
    promise?.resolve(TcSdk.getInstance().isOAuthFlowUsable)
  }


  override fun clearSDK() {
    TcSdk.clear()
  }


  private val tcOAuthCallback = object : TcOAuthCallback {
    override fun onVerificationRequired(tcOAuthError: TcOAuthError?) {
      Log.i(TAG, "onVerificationRequired")
    }

    override fun onSuccess(tcOAuthData: TcOAuthData) {
      val map = Arguments.createMap()
      map.putBoolean("successful", true)
      map.putString("code_verifier", codeVerifier)
      map.putString("code", tcOAuthData.authorizationCode)
      promise?.resolve(map)
      promise = null
    }

    override fun onFailure(tcOAuthError: TcOAuthError) {
      val map = Arguments.createMap()
      map.putBoolean("successful", false)
      map.putString("error", tcOAuthError.toString())
      promise?.resolve(map)
      promise = null
    }
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
//  override fun multiply(a: Double, b: Double): Double {
//    return a * b
//  }

  companion object {
    private const val TAG = "NativeTrueCaller"
    const val NAME = "Truecaller"
  }
}
