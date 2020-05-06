var context;
var collection;
var element;
var tags;

if(element.metadata.foo) context.attachElement(element, 'All foos');

if(element.tags.includes('yada')) context.attachElement(element, 'A yada');

context.attachElement(element, 'Chronological', element.entryData.toString());
