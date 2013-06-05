Welcome to project GENESIS.
=============================

This piece of software is currently under development and will increase in size, quality and functionality!

We are team JAMES, which is an acronym for our names. But what's more important is what we are developing: A piece of software that will enable biologists, or actually everyone, to design and simulate protein chain models. So now you can design your own micro-processes using proteins!

The idea behind this is that proteins could somewhat behave like electrical circuits, but inside a cell! Certain combinations of proteins behave like AND gates, while other proteins behave like NOT gates. This enables the creation of logical circuits that also can be simulated.

However, since this is an abstraction of the mechanics behind proteins, this model is not very representative for a real-life application. Though purely scientific, it's also fun to play around with!


Here's a step-by-step guide to the GENESIS system:
<ol>
  <li>Make sure you have credentials!</li>
  <li>Log into the login page, check the remember me checkbox if necessary.</li>
  <li>Now you will be confronted with a setup modal. Pick the library you want to use. These are bound to your account or public.</li>
  <li>Next you will have to name your circuit. No need to think of very complex names, these names only have to be unique within your account</li>
  <li>You also need a timespan to simulate along. This is in seconds.</li>
  <li>And finally give in the number of steps you want to simulate over, the more steps, the more detailed but also simulation will take longer. Click "Apply setup" to finish the setup and close the modal.</li>
  <li>Now you will see an input and an output gate. These will act as your inpout and output! Drag and drop the corresponding elements on the right into the workspace to create a new gate instance.</li>
  <li>When you create a new instance, endpoints are added to the gate. Use these to connect your gates, but remember, gates have inputs and ouputs, and they can only be dragged in one way!</li>
  <li>When two gates are linked up, you can click that connection to select the protein that should be used in that connection. Be carefull not to chose the same protein multiple times.</li>
  <li>When you are content with your circuit, you can click on "Simulation" and then "Run circuit" to start the simulation process.</li>
  <li>But first you must input the input vectors, more will be explained in section <strong>Inputs</strong></li>
  <li>Finally you click "Apply" and the simulation will start. When done you will be presented with a graph and a button to download a .csv with the results.</li>
  <li>Click on "Circuit" and then "Save" to save yor circuit.</li>
  <li>Or, if you want to load a circuit, click "Circuit" and then "Load". You will be presented with a list of your (or public) circuits you can load, and then edit or simulate.</li>
</ol>
