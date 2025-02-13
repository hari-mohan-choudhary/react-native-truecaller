# rn-truecaller

rn-truecaller is a React Native package for integrating Truecaller login with the new architecture.

## Installation

Use the package manager npm or yarn to install rn-truecaller

```bash
npm install rn-truecaller
```
or
```bash
yarn add rn-truecaller
```

## Usage

```python
import TruecallerSDK from 'rn-truecaller';

// Initialize the SDK
TruecallerSDK.initSDK().then((isInitialized) => {
  if (isInitialized) {
    console.log('Truecaller SDK initialized');
  }
});

// Authenticate the user
TruecallerSDK.authenticate()
  .then((response) => {
    console.log('User authenticated:', response);
  })
  .catch((error) => {
    console.error('Authentication failed:', error);
  });

// Check if Truecaller is installed
TruecallerSDK.isTrueCallerAppInstall().then((isInstalled) => {
  console.log('Is Truecaller Installed:', isInstalled);
});

// Clear SDK
TruecallerSDK.clearSDK();
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)