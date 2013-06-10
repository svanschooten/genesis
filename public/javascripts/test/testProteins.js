test("test test", function(){
    ok(2 == 2, "Testing works!");
})

test("JSON getLibrary test", function(){
    getLibrary(0);
    notEqual(selectedLibrary, -1, "Library has been fetched.");
    notEqual(cdsMap, {}, "No empty cds.");
    notEqual(andMap, {}, "No empty and.");
});

test("Parse library", function(){
    cdsMap = {};
    equal(cdsMap, {}, "cds is reset.");
    andMap = {};
    equal(andMap, {}, "and is reset.");
    var lib = {"and":[{"input1":"A","input2":"B","k1":4.5272,"km":238.9569,"n":3}],"not":[{"A":{"k1":4.7313,"km":224.0227,"n":1}},{"B":{"k1":2.8753,"km":281.3545,"n":1}}],"cds":[{"A":{"k2":4.6337,"d1":0.024,"d2":0.8466}},{"B":{"k2":4.6122,"d1":0.0205,"d2":0.8627}}]};
    parseLibrary(JSON.stringify(lib));
    notEqual(cdsMap, {}, "cds has been filled.");
    notEqual(andMap, {}, "and has been filled.");
    deepEqual(andMap.A.B, {name: "B", pk1: 4.5272, pkm: 238.9569, pn: 3}, "And protein tuple parse correctly.");
    deepEqual(cdsMap.A, {name: "A", pd1: 0.024, pd2: 0.8466, pk1: 4.7313, pk2: 4.6337, pkm: 224.0227, pn: 1}, "And protein first tuple parse correctly.");
    deepEqual(cdsMap.B, {name: "B", pd1: 0.0205, pd2: 0.8627, pk1: 2.8753, pk2: 4.6122, pkm: 281.3545, pn: 1}, "And protein second tuple parse correctly.");
});