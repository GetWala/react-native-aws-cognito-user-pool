using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Com.Getwala.ReactNativeAwsCognitoUserPool
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class ReactNativeAwsCognitoUserPoolModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="ReactNativeAwsCognitoUserPoolModule"/>.
        /// </summary>
        internal ReactNativeAwsCognitoUserPoolModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "ReactNativeAwsCognitoUserPool";
            }
        }
    }
}
