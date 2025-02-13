import type { TurboModule } from 'react-native';
export interface Spec extends TurboModule {
    initSDK(): Promise<boolean>;
    authenticate(): Promise<any>;
    isTrueCallerAppInstall(): Promise<boolean>;
    clearSDK(): void;
}
declare const _default: Spec;
export default _default;
//# sourceMappingURL=NativeTruecaller.d.ts.map