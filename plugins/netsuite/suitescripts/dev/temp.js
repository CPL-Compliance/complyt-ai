/**
 * @NApiVersion 2.1
 */
 define(['N/ui/dialog'], (dialog) => {
  let options = {
      title: 'I am an Alert',
      message: 'Click OK to continue.'
  };

  function success(result) {
      console.log('Success with value ' + result);
  }

  function failure(reason) {
      console.log('Failure: ' + reason);
  }

  dialog.alert(options).then(success).catch(failure);
}); 