/**
 * @file Integration tests for the main view.
 * @author Eric Weinstein <eric.q.weinstein@gmail.com>
 */

describe('Home page', function() {
  'use strict';

  browser.get('http://localhost:8080/');

  it('includes the CityShelf tagline', function() {
    expect(browser.isElementPresent(by.tagName('h1'))).toBe(true);
  });

  it('includes the search form', function() {
    expect(browser.isElementPresent(by.id('search'))).toBe(true);
  });

  it('includes the social media icons', function() {
    expect(browser.isElementPresent(by.id('facebook'))).toBe(true);
    expect(browser.isElementPresent(by.id('twitter'))).toBe(true);
    expect(browser.isElementPresent(by.id('instagram'))).toBe(true);
    expect(browser.isElementPresent(by.id('email'))).toBe(true);
  });

  it('allows users to search', function() {
    var input  = element(by.model('form.book'));

    input.sendKeys('omon ra');
    input.sendKeys(protractor.Key.ENTER);

    expect(browser.getCurrentUrl()).toContain('search');
    expect(browser.isElementPresent(by.css('.book-img-wrapper'))).toBe(true);
  });
});
