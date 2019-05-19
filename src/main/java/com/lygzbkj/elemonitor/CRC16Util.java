package com.lygzbkj.elemonitor;

import java.util.Arrays;

public class CRC16Util {

	public static int getCRC(byte[] by) {
		int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < by.length; i++) {
            CRC ^= ((int) by[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        String strCRC = Integer.toHexString(CRC);
        System.out.println("CRC16Util " + strCRC);
        return CRC;
	}
	
	/**
	 * 检验crc16校验是否正确
	 * 报文中除了校验和其他全包括, 包括长度和通信机好
	 * @param by 包含校验和的数组, 校验和两个字节,先低后高
	 * @return
	 */
	public static boolean checkCRC(byte[] by) {
		if(by.length < 3) {
			return false;
		}
		int crc = getCRC(Arrays.copyOfRange(by, 0, by.length - 2));
		int byCRC = Util.bytesToInt(new byte[] {by[by.length - 1], by[by.length - 2]});
		return crc == byCRC;
	}
	
	public static void main(String[] args) {
//		byte[] by = new byte[] {0, 0x19, 0x40, 0, 0, 0, 0, 1, 0, 0, 0, 0x13, 0, (byte) 0xb5, (byte) 0xec, (byte) 0xc9, 0, 0x34, (byte) 0xd0, (byte) 0x91, 0, (byte) 0xbf, 0x28, 3, 7, (byte) 0xe3, 3, 0x1d, 9, 0x1d, 0x29};
//		int crc = getCRC(by);
//		byte[] bycrc = new byte[] {(byte) 0xad, 0x2b};
//		int byCrc = Util.bytesToInt(bycrc);
//		int byCrc2 = Util.bytesToInt(new byte[] {bycrc[1], bycrc[0]});
//		System.out.println("CRC16Util " + (crc == byCrc));
//		System.out.println("CRC16Util 2 " + (crc == byCrc2));
		
		byte[] by = new byte[] {0, 0x19, 0x00, 0, 0, 3, 0, 1, 0, 0, 0, 0x13, 0, (byte) 0xb5, (byte) 0xec, (byte) 0xc9, 0, 0x34, (byte) 0xd0, (byte) 0x91, 0, (byte) 0xbf, 0x28, 3, 7, (byte) 0xe3, 3, 0x1d, 9, 0x1d, 0x29, (byte) 0xa6, 0x1b};
		boolean res = checkCRC(by);
		System.out.println("CRC16Util " + res);
	}
}
