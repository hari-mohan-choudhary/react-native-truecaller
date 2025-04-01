import { Platform } from "react-native";
import type { TurboModule } from "react-native";
import { TurboModuleRegistry } from "react-native";

// Define the Spec interface
export interface Spec extends TurboModule {
  initSDK(): Promise<boolean>;
  authenticate(): Promise<any>;
  isTrueCallerAppInstall(): Promise<boolean>;
  clearSDK(): void;
}

// Android: Load the native module
const NativeTruecaller =
  Platform.OS === "android"
    ? TurboModuleRegistry.getEnforcing<Spec>("Truecaller")
    : null;

// iOS: Provide a mock implementation to prevent crashes
const iOSFallback: Spec = {
  initSDK: async () => {
    console.warn("Truecaller module is not available on iOS");
    return false;
  },
  authenticate: async () => {
    console.warn("Truecaller module is not available on iOS");
    return null;
  },
  isTrueCallerAppInstall: async () => {
    console.warn("Truecaller module is not available on iOS");
    return false;
  },
  clearSDK: () => {
    console.warn("Truecaller module is not available on iOS");
  },
};

// Export the correct module based on the platform
export default NativeTruecaller ?? iOSFallback;
