/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaagenttask;

import java.lang.instrument.Instrumentation;

/**
 *
 * @author Andrii_Kozak1
 */
public class Agent {

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new StacktraceClassTransformer());
    }
}
