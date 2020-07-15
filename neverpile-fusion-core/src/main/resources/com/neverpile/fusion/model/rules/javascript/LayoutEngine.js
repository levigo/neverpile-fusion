// init the collection related global variables
function _initCollection() {
	_cd(collection); 
	collection.elements.forEach(_cd);
}

// init the element-related global variables
function _initElement() {
	// declare those in global scope
	element = collection.elements[_elementIndex];
	tags = element.tags;
}

// convert dates to "real" dates of collection and element objects
function _cd(o){
	o.dateCreated = new Date(o.dateCreated); 
	o.dateModified = new Date(o.dateModified);
}

// wrap a Java Node object so that the withProperty method replaces the JS element with the java one  
function _wrapNode(n) {
	var wrapper = {
		// wrap the withElement method to replace the JS element with the Java one
		withElement: function(e) {
			return n.withElement(_collection.elements.get(collection.elements.indexOf(e)));
		},
		createElementNode: function(e) {
			return _wrapNode(n.createElementNode(_collection.elements.get(collection.elements.indexOf(e))));
		},
		withProperty: function(k, v) {
			n.withProperty(k, v);
			return wrapper;
		},
		withVisualization: function(k, v) {
			n.withVisualization(k, v);
			return wrapper;
		},
    createNode: function() {
      var path = Array.prototype.slice.call(arguments) 
      return _wrapNode(n.createPath(path)); 
    },
    initiallyExpanded: function() { n.initiallyExpanded(); return wrapper; },
    initiallyCollapsed: function() { n.initiallyCollapsed(); return wrapper; },
		properties: n.properties,
		children: n.children
	};

	return wrapper;
}
      
// the rule "API"
function createNode() {
	var path = Array.prototype.slice.call(arguments);
	return _wrapNode(_root.createPath(path));
}
function findNode() {
	var path = Array.prototype.slice.call(arguments);
	return _wrapNode(_root.findNode(path));
}
function withNode(f) {
	var path = Array.prototype.slice.call(arguments).slice(1);
	var node = _root.findNode(path);
	if(node) {
		f(_wrapNode(node));
	}
}
function createElementNode(e) {
	var path = Array.prototype.slice.call(arguments).slice(1);
	return _root.createPath(path).createElementNode(
		_collection.elements.get(collection.elements.indexOf(e))
	);
}
function putNodeProperty(n, v) {
	var path = Array.prototype.slice.call(arguments).slice(2);
	_root.createPath(path).getProperties().put(n, v);
}
      
// Array.includes() polyfill
// https://tc39.github.io/ecma262/#sec-array.prototype.includes
if (!Array.prototype.includes) {
  Object.defineProperty(Array.prototype, 'includes', {
    value: function(searchElement, fromIndex) {

      if (this == null) {
        throw new TypeError('"this" is null or not defined');
      }

      // 1. Let O be ? ToObject(this value).
      var o = Object(this);

      // 2. Let len be ? ToLength(? Get(O, "length")).
      var len = o.length >>> 0;

      // 3. If len is 0, return false.
      if (len === 0) {
        return false;
      }

      // 4. Let n be ? ToInteger(fromIndex).
      //    (If fromIndex is undefined, this step produces the value 0.)
      var n = fromIndex | 0;

      // 5. If n ≥ 0, then
      //  a. Let k be n.
      // 6. Else n < 0,
      //  a. Let k be len + n.
      //  b. If k < 0, let k be 0.
      var k = Math.max(n >= 0 ? n : len - Math.abs(n), 0);

      function sameValueZero(x, y) {
        return x === y || (typeof x === 'number' && typeof y === 'number' && isNaN(x) && isNaN(y));
      }

      // 7. Repeat, while k < len
      while (k < len) {
        // a. Let elementK be the result of ? Get(O, ! ToString(k)).
        // b. If SameValueZero(searchElement, elementK) is true, return true.
        if (sameValueZero(o[k], searchElement)) {
          return true;
        }
        // c. Increase k by 1. 
        k++;
      }

      // 8. Return false
      return false;
    }
  });
}