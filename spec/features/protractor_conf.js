// @file Configuration for Protractor, Angular's end-to-end test runner.
// @author Eric Weinstein <eric.q.weinstein@gmail.com>

exports.config = {
  // The address of a running Selenium server.
  seleniumAddress: 'http://localhost:4444/wd/hub'

  // Capabilities to pass to the webdriver instance.
, capabilities: {
    'browserName': 'chrome'
  }

  // Spec patterns are relative to the location of the spec file.
  // They can include glob patterns.
, specs: ['./**/*_spec.js']

  // Options to pass to the Jasmine-Node.
, jasmineNodeOpts: {
    showColors: true
  }
};
