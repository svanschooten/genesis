test ( "hello test", function(){
	ok(2 == 2, "Test if testing works!");
});

test("findElement test 1", function(){
	equal(findElement([], 1), null, "Empty array returns null");
	equal(findElement([{id: 2}], -1), null, "Negative index parameter returns null")
	equal(findElement(['a'], 1), null, "Array with invalid contents returns null")
	deepEqual(findElement([{id: 3}], 3), {id: 3}, "Array with valid contents returns the object")
});