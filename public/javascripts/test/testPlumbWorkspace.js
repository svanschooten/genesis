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
	var invalidProtein1 = new Protein(-1, "data");
	var invalidProtein2 = new Protein(0, 32);
	equal(validProtein.id, 0, "Protein has correct id.");
	equal(validProtein.data, "data", "Protein has correct data.");
	notEqual(invalidProtein1.id, -1, "Invalid protein (by id) has not been constructed.");
	notEqual(invalidProtein2.data, 32, "Invalid protein (by data) has not been constructed.");
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
	equal(domGates.length, 3, "Gates created and stored correctly.");
});

test("Workspace setup domtest", function(){
    ok(gin == null, "No input created.");
    ok(gout == null, "No output created.");
    makeInput();
    notEqual(document.getElementById("input"), null, "Input element has been created.");
    makeOutput();
    notEqual(document.getElementById("output"), null, "Output element has been created.");
});

test("jsPlumb parsetest", function(){
    var and = andGate(0,0);
    var not = notGate(0,0);
    jsPlumb.connect({
        "source": jsPlumb.getEndpoints(and.id).filter(filterFn("isSource"))[0],
        "target": jsPlumb.getEndpoints(not.id).filter(filterFn("isTarget"))[0]
    });
    var jspRes = parseJsPlumb();
    notEqual(jspRes, null, "Parsed memory model is not empty.");
    equal(jspRes.edges.length, 1, "One connection made and parsed.");
    deepEqual(jspRes.edges[0], {protein: "", source: and.id, target: not.id}, "Connection correctly stored.");
    equal(jspRes.vertices.length, 7, "All gates have been added.");
});