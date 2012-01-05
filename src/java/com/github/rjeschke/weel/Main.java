/*
 * Copyright (C) 2011 René Jeschke <rene_jeschke@yahoo.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.rjeschke.weel;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println(System.getProperty("java.vm.name") + " v" + System.getProperty("java.vm.version"));
        try
        {
            final Weel weel = new Weel();
            weel.setDebugMode(false);
            weel.enableCodeDump(true);

//            weel.compileResource("com.github.rjeschke.weel.test.bench_fib_recursive");
//            weel.compileResource("com.github.rjeschke.weel.test.lsys");
//            weel.compileResource("com.github.rjeschke.weel.test.bench1");
//            weel.compileResource("com.github.rjeschke.weel.test.mandel");
//            weel.compileResource("com.github.rjeschke.weel.test.test2");
            weel.compileResource("com.github.rjeschke.weel.test.fact");
//            weel.compileResource("com.github.rjeschke.weel.test.wunitArith");

//            for(WeelLoader.ClassData cd : weel.classLoader.classData)
//            {
//                FileOutputStream fos = new FileOutputStream(
//                        "/home/rjeschke/" + cd.name.substring(cd.name.lastIndexOf('.') + 1) + ".class");
//                fos.write(cd.code);
//                fos.close();
//            }

//            for(final WeelFunction f : weel.functions)
//            {
//                System.out.println(f.index + " : " + f.toFullString());
//            }
            
            weel.runStatic();
            
//            System.gc();
//            weel.runMain("10", "5000");
            //weel.runMain("10000", "15");
            weel.runMain("5", "37");
            //weel.getRuntime().wipeStack();
//            WeelUnit.runTests(weel);
            if(weel.getRuntime().getStackPointer() != -1)
                System.err.println("Doh! You messed it up! (" + weel.getRuntime().getStackPointer() + ")");
        }
        catch (Exception e)
        {
            throw (e instanceof WeelException) ? (WeelException) e
                    : new WeelException(e);
        }
    }
}
