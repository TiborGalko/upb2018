/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package upb.upb2018.z3;

/**
 *
 * @author Lenovo
 */
public class BytesToHex {

  public static String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b: bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

}
