a = Angel.new();

b = BSP.new();

b.pads;
b.knobs;
b.steps;


// register a function to be evaluted when fader1 is changed
b.knob1.on = {|val| (val/127).postln };

// overwrite the previous assignment
b.knob1.on = {|val| val.linexp(0, 127, 20, 20000).postln };

b.pad1.on = { "Hello, ".postln };
b.pad1.off = { "Arturia BeatStep!".postln };

b.pad2.on = {"whatup".postln};

b.step1.on = { "Step me".postln};
b.steps[7].on = { "Step all over me".postln}; // note zero index

MIDIdef.all

