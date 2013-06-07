test ( "hello test", function(){
	ok(2 == 2, "Test if testing works!");
});

test("Find Element", function(){
	equal(findElement([], 1), null, "Empty array returns null.");
	equal(findElement([{id: 2}], -1), null, "Negative index parameter returns null.")
	equal(findElement(['a'], 1), null, "Array with invalid contents returns null.")
	deepEqual(findElement([{id: 3}], 3), {id: 3}, "Array with valid contents returns the object.")
});

test("Protein constructor", function(){
	var validProtein = new Protein(0, "data");
	equal(validProtein.id, 0, "Protein has correct id.");
});

test("Gate constructor", function(){
	deepEqual(circuit, new Array(), "Circuit is empty.");
	
	var validGate = new Gate("test-gate", 0, 0, null, 5, 5);
	
	equal(validGate.id, "test-gate0", "Gate id is correct.");
	equal(validGate.type, "test-gate", "Gate type is correct.")
	ok(circuit.length == 1, "Circuit is filled after creating a new gate.")
});

test("Gate constructor domtest", function(){
	var gate = new Gate("other-test-gate", 0, 0, null, 0, 0);
	var imageGate = new Gate("image-test-gate", 0, 0, "assets/images/NOT_gate.png", 0, 0);
	
	var domGates = document.getElementsByClassName('gateElement');
	ok(2 ==2, "Ok!");
});