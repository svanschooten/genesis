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
	var invalidProtein = new Protein(-1, "data");
	equal(validProtein.id, 0, "Protein has correct id.");
	equal(validProtein.data, "data", "Protein has correct data.");
	deepEqual(invalidProtein, {}, "Invalid protein (by id) has not been constructed.");
});

test("Gate constructor", function(){
    hardReset();
    var initCircuit = [{id: "input", type: "input", x: 0, y: 0},{id: "output", type: "output", x: 0, y: 0}]
	deepEqual(circuit, initCircuit, "Circuit is empty, except for input and output.");
	var validGate = new Gate("test-gate", 0, 0, null, 5, 5);
	equal(validGate.id, "test-gate2", "Gate id is correct.");
	equal(validGate.type, "test-gate", "Gate type is correct.");
	equal(circuit.length, 3, "Circuit is filled after creating a new gate, input and output.");
	hardReset();
});

test("Gate constructor domtest", function(){
	new Gate("other-test-gate", 0, 0, null, 0, 0);
	new Gate("image-test-gate", 0, 0, "assets/images/NOT_gate.png", 0, 0);
	equal(circuit.length, 4, "Gates created and stored correctly.");
	hardReset();
});

test("Workspace setup domtest", function(){
    notEqual(gin, null, "No input created.");
    notEqual(gout, null, "No output created.");
    makeInput();
    notDeepEqual($("#input"), null, "Input element has been created.");
    makeOutput();
    notDeepEqual($("#output"), null, "Output element has been created.");
});

test("jsPlumb parsetest", function(){
    var and = andGate(0,0);
    var not = notGate(0,0);
    var jspRes = parseJsPlumb();
    notDeepEqual(jspRes, null, "Parsed memory model is not empty.");
    equal(jspRes.edges.length, 0, "No connections.");
    deepEqual(jspRes.edges[0], {protein: "", source: and.id, target: not.id}, "Connection correctly stored.");
    equal(jspRes.vertices.length, 7, "All gates have been added.");
});

test("jsPlumb reset workspace", function(){
    notEqual($("#plumbArea"[0], null, "Workspace is not empty."));
    hardReset();
    ok(circuit.length == 2, "Circuit is empty again, only input and output remain.");
    ok(currentConnection == null, "No currently selected connection.");
    ok(inputs == "", "Inputs have been removed.");
    ok($("#plumbArea").children().length == 2, "Only input and output remain in DOM.");
});