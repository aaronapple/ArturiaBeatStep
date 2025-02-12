// ===========================================================================
// Title         : ArturiaBeatStep
// Description   : Controller class for Arturia BeatStep
// Version       : 1.0alpha
// Copyright (c) : David Granström 2015
// ===========================================================================

BSP {
    var <chan, <knobs, <pads, <steps;

    var ctls;
    var knobValues, padValues, stepValues;
    var mOut;

    *new { | chan=0 |
        ^super.newCopyArgs(chan).init;
    }

    init {
        ctls = ();

        knobs = List[];
        pads  = List[];
        steps  = List[];

        knobValues = [ 10, 74, 71, 76, 77, 93, 73, 75, 114, 18, 19, 16, 17, 91, 79, 72 ];
        padValues  = [ 44, 45, 46, 47, 48, 49, 50, 51, 36, 37, 38, 39, 40, 41, 42, 43 ];
        stepValues = (20 .. 31) ++ (52 .. 55); // these are ccs?

        MIDIClient.init;
        MIDIIn.connect(0, MIDIClient.externalSources.select({|x| x.name == "Arturia BeatStep Pro Arturia Be"})[0]);
        mOut = MIDIOut.newByName("Arturia BeatStep Pro", "Arturia BeatStep Pro Arturia Be").latency_(0.0);

        this.assignCtls;
    }

    assignCtls {
        knobValues.do {|cc, i|
            var key  = ("knob" ++ (i+1)).asSymbol;
            var knob = ABSKnob(key, cc, chan);
            knobs.add(knob);
            ctls.put(key, knob);
        };

        padValues.collect {|note, i|
            var key = ("pad" ++ (i+1)).asSymbol;
            var pad = ABSPad(key, note, chan);
            pads.add(pad);
            ctls.put(key, pad);
        };

        stepValues.do {|note, i|
            var key  = ("step" ++ (i+1)).asSymbol;
            var step = ABSStep(key, note, chan);
            steps.add(step);
            ctls.put(key, step);
        };
    }

    freeAll {
        ctls.do(_.free);
    }

    doesNotUnderstand {|selector ... args|
        ^ctls[selector] ?? { ^super.doesNotUnderstand(selector, args) }
    }
}

ABSKnob {
    var key, cc, chan;

    *new {|key, cc, chan|
        ^super.newCopyArgs(("abs_" ++ key).asSymbol, cc, chan);
    }

    on_ {|func|
        MIDIdef.cc(key, {|val, num| func.(val, num)}, cc, chan);
    }

    free {
        MIDIdef.cc(key).free;
    }
}

ABSPad {
    var key, note, chan;

    *new {|key, note, chan|
        ^super.newCopyArgs("abs_" ++ key, note, chan);
    }

    on_ {|func|
        MIDIdef.noteOn((key ++ "_on").asSymbol, {|val, num| func.(val, num)}, note, chan);
    }

    off_ {|func|
        MIDIdef.noteOff((key ++ "_off").asSymbol, {|val, num| func.(val, num)}, note, chan);
    }

    free {
        [ "_on", "_off"].do {|label|
            var k = (key ++ label).asSymbol;
            MIDIdef.cc(k).free;
        };
    }
}

ABSStep {
    var key, cc, chan;

    *new {|key, cc, chan|
        ^super.newCopyArgs(("abs_" ++ key).asSymbol, cc, chan);
    }

    on_ {|func|
        MIDIdef.cc(key, {|val, num| if(val == 127, {func.(val, num)},{})}, cc, chan);
    }

    free {
        MIDIdef.cc(key).free;
    }
}
