import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  initSDK(): Promise<boolean>;
  authenticate(): Promise<any>;
  isTrueCallerAppInstall(): Promise<boolean>;
  clearSDK(): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Truecaller');
