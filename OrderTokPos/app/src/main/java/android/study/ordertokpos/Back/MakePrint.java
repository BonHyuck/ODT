package android.study.ordertokpos.Back;

import android.content.Context;
import android.util.Log;


public class MakePrint {
    public final static String LF = String.format("%c", 0x0a);
    public final static String AL = String.format("%c", 0x1b) + String.format("%c", 0x61) + String.format("%c", 0x00);
    public final static String AC = String.format("%c", 0x1b) + String.format("%c", 0x61) + String.format("%c", 0x01);
    public final static String AR = String.format("%c", 0x1b) + String.format("%c", 0x61) + String.format("%c", 0x02);
    public final static String UL_ON = String.format("%c", 0x1b) + String.format("%c", 0x2d) + String.format("%c", 0x02);
    public final static String UL_OFF = String.format("%c", 0x1b) + String.format("%c", 0x2d) + String.format("%c", 0x00);
    public final static String IVT_ON = String.format("%c", 0x1d) + String.format("%c", 0x42) + String.format("%c", 0x01);
    public final static String IVT_OFF = String.format("%c", 0x1d) + String.format("%c", 0x42) + String.format("%c", 0x00);
    public final static String X1 = String.format("%c", 0x1b) + String.format("%c", 0x21) + String.format("%c", 0x02);
    public final static String X2 = String.format("%c", 0x1b) + String.format("%c", 0x21) + String.format("%c", 0x30);
    public final static String W2 = String.format("%c", 0x1b) + String.format("%c", 0x21) + String.format("%c", 0x20);
    public final static String BC = String.format("%c", 0x1d) + String.format("%c", 0x68) + String.format("%c", 0x40) + String.format("%c", 0x1d) + String.format("%c", 0x77) + String.format("%c", 0x02) + String.format("%c", 0x1d) + String.format("%c", 0x6B) + String.format("%c", 0x49);
    public final static String CUT = String.format("%c", 0x1b) + String.format("%c", 0x69);

    /**
     *
     * @param ptrdate 	거래일시		YYMMDDhhmmss
     * @param ptrtype	거래구분제목
     * @param pcash		현금영수증구분		false:신용, true:현금영수증
     * @param pcancel	취소구분			false:승인, true:취소
     * @param pamount	총 금액
     * @param ptax		부가세
     * @param ptip		봉사료
     * @param pinstallment	할부
     * @param pcardno	카드번호
     * @param pshopname	가맹점이름
     * @param pshopbiznum	가맹점 사업자번호
     * @param pshopowner	가맹점 대표
     * @param pshoptelnum	가맹점 전화번호
     * @param pshoptid	단말기번호
     * @param pshopaddress	가맹점 주소
     * @param pissuer	카드발급사
     * @param pmembernum	가맹번호
     * @param poriaprdate	원승인일자
     * @param poriaprno	원승인번호
     * @param papprovalno	승인번호
     * @param pacquirername	 매입사명
     * @param pnotice	notice
     * @param ptailmsg	꼬리말
     * @param psign		서명 유무			false:없음, true:있음
     * @return
     */
    ///////////////////////////////////////////////////////////
    //		ptrdate			String		: 거래일시		YYMMDDhhmmss
    //		ptrtype						: 거래구분제목
    //		pcash			boolean		: 현금영수증구분		false:신용, true:현금영수증
    //		pcancel			boolean		: 취소구분			false:승인, true:취소
    //		pamount			integer		: 총 금액
    //		ptax			integer		: 부가세
    //		ptip			integer		: 봉사료
    //		pinstallment	integer		: 봉사료
    //		pcardno			String		: 카드번호
    //		pshopname		String		: 가맹점이름
    //		pshopbiznum		String		: 가맹점 사업자번호
    //		pshopowner		String		: 가맹점 대표
    //		pshoptelnum		String		: 가맹점 전화번호
    //		pshoptid		String		: 단말기번호
    //		pshopaddress	String		: 가맹점 주소
    //		pissuer			String		: 카드발급사
    //		pmembernum		String		: 가맹번호
    //		poriaprdate		String		: 원승인일자
    //		poriaprno		String		: 원승인번호
    //		papprovalno		String		: 승인번호
    //		pacquirername	String		: 매입사명
    //		pnotice			String		: notice
    //		padvertise		String		: advertise
    //		ptailmsg		String		: 꼬리말
    //		psign 			boolean		: 서명 유무			false:없음, true:있음
    public static String receiptPrint(
            String ptrdate,
            String ptrtype,
            boolean pcash,
            boolean pcancel,
            int pamount,
            int ptax,
            int ptip,
            int pinstallment,
            String pcardno,
            String pshopname,
            String pshopbiznum,
            String pshopowner,
            String pshoptelnum,
            String pshoptid,
            String pshopaddress,
            String pissuer,
            String pmembernum,
            String poriaprdate,
            String poriaprno,
            String papprovalno,
            String pacquirername,
            String pnotice,
            String ptailmsg,
            boolean psign)
    {
        ////// printmode ----- 0:결과화면출력 / 1:거래내역인쇄
        ///// 거래 일시
        String trdate = ptrdate;
        String trdate_r = "";
        try{
            trdate_r = "20" 	+ trdate.substring(0, 2) + "/"
                    + trdate.substring(2, 4) + "/"
                    + trdate.substring(4, 6) + "  "
                    + trdate.substring(6, 8) + ":"
                    + trdate.substring(8,10) + ":"
                    + trdate.substring(10,12);
        }catch(Exception e){
            Log.e("ET-223","Date Error : Must 12 length over");
        }

        int intTotalAmount = pamount;
        int intTax = ptax;
        int intTip = ptip;
        int intAmount = 0;

        ///// 거래 금액
        String amount = "";
        intAmount = intTotalAmount - intTax - intTip;
        if(pcancel)
            amount = "-";

        amount += String.format("%,d",intAmount);
        amount += "원";

        int amount_len = getByteSizeToComplex(amount);
        amount = ("                              "+amount).substring(amount_len+1);

        ///// 부가세
        String Tax = "";

        if(pcancel)
            Tax = "-";

        Tax += String.format("%,d",intTax);
        Tax += "원";

        int tax_len = getByteSizeToComplex(Tax);
        Tax = ("                              "+Tax).substring(tax_len+1);

        ///// 봉사료
        String Tip = "";
        if(pcancel)
            Tip = "-";

        Tip += String.format("%,d",intTip);
        Tip += "원";

        int tip_len = getByteSizeToComplex(Tip);
        Tip = ("                              "+Tip).substring(tip_len+1);


        ///// 합계
        String Total = "";
        if(pcancel)
            Total = "-";

        Total += String.format("%,d",intTotalAmount);
        Total += "원";

        int total_len = getByteSizeToComplex(Total);
        Total = ("              "+Total).substring(total_len);

        ////// 할부
        String install = "";
        if(pinstallment < 1)
        {
            install = "일시불";
        }
        else
        {
            String sInstallment = String.valueOf(pinstallment);
            sInstallment += " 개월";
            install = sInstallment;
        }

        String card_Num = pcardno;
        if(card_Num.length() > 16){
            card_Num = card_Num.substring(0, 4)
                    + "-"
                    + card_Num.substring(5, 7)
                    + "**"
                    + "-"
                    + "****"
                    + "-"
                    + card_Num.substring(15);
        }


        String print_msg = "";
        print_msg += LF;
        print_msg += AL + pshopname + LF;
        String tmpshopbiznum = pshopbiznum;
        print_msg += AL + tmpshopbiznum.substring(0, 3) + "-"
                + tmpshopbiznum.substring(3, 5) + "-"
                + tmpshopbiznum.substring(5) + "  " + pshopowner + LF;
        print_msg += AL + pshoptelnum + "  " + pshoptid + LF;
        print_msg += AL + pshopaddress + LF;
        print_msg += "------------------------------------------" + LF;


        print_msg += W2 + AC + ptrtype + X1 + LF;

        if(!pcash){
            print_msg += AL + "카드  종류 : " + pissuer + LF;
            if(pmembernum.length()>0)
                print_msg += AL + "가맹  번호 : " + pmembernum + LF;
            print_msg += AL + "거래  구분 : " + install + LF;
            print_msg += AL + "카드  번호 : " + card_Num + LF;
        }else{
            print_msg += AL + "카드  종류 : " + pissuer + LF;
            print_msg += AL + "식별  번호 : " + card_Num + LF;
        }


        print_msg += AL + "거래  일시 : " + trdate_r + LF;
        print_msg += "------------------------------------------" + LF;
        print_msg += AL + "거래  금액 :" + amount + LF;
        if(intTax>0)
            print_msg += AL + "부  가  세 :" + Tax + LF;
        if(intTip>0)
            print_msg += AL + "봉  사  료 :" + Tip + LF;
        print_msg += AL + "합      계 : " + W2 + Total + X1 + LF;
        print_msg += "------------------------------------------" + LF;


        //##########################################


        if(pcancel)
        {
            print_msg += AL + "원승인일자 : " + "20" + poriaprdate.substring(0, 2) + "/"
                    + poriaprdate.substring(2, 4) + "/"
                    + poriaprdate.substring(4) + LF;
            print_msg += AL + "원승인번호 : " + poriaprno.substring(0, 4) + " " + poriaprno.substring(4) + LF;

        }
        //##########################################



        print_msg += AL + "승인  번호 : " + W2 + papprovalno.substring(0, 4) + " " + papprovalno.substring(4) + X1 + LF;

        if(pacquirername.length() > 0)
            print_msg += AL + "매  입  사 : " + pacquirername + LF;
        print_msg += LF;
        print_msg += AL + cutNotice(pnotice) + LF;
        print_msg += LF;

//			if(padvertise.length() > 0)
//				print_msg += AL + AdData(padvertise) + LF;

        print_msg += AR + W2 + ptailmsg + X1 + LF;

        print_msg += LF;
        print_msg += LF;
        print_msg += LF + CUT;

        return print_msg;
    }

    @SuppressWarnings("unused")
    private String AdData(String addata){
        String rtn = "";
        String rtnmsg = "";
        char[] string;

        for (int i = 0 ; i < addata.length(); i++){
            rtn = addata.substring(i, i+1);
            string = rtn.toCharArray();
            if(string[0] == String.format("%c",0x0e).charAt(0)){
                rtnmsg += X2;
            }else if(string[0] == String.format("%c",0x0f).charAt(0)){
                rtnmsg += X1;
            }else
                rtnmsg += rtn;
        }

        return rtnmsg;
    }

    private static String cutNotice(String notice){
        String rtn = "";
        int len = 0;
        boolean line1 = false;
        boolean line2 = false;
        boolean line3 = false;
        for (int i = 0 ; i < notice.length(); i++){
            rtn += notice.substring(i, i+1);
            if(getByteSizeToComplex(notice.substring(i, i+1))==1){
                len++;
            }else{
                len += 2;
            }
            if(len == 14 && !line1){
                rtn += LF;
                line1 = true;
            }

            if(len > 43 && !line2){
                rtn += LF;
                line2 = true;
            }

            if(len > 73 && !line3){
                rtn += LF;
                line3 = true;
            }

        }
        return rtn;
    }

    private static int getByteSizeToComplex(String str) {

        int en = 0;
        int ko = 0;
        int etc = 0;

        char[] string = str.toCharArray();

        for (int j=0; j<string.length; j++) {
            if (string[j]>='A' && string[j]<='z') {
                en++;
            }
            else if (string[j]>='\uAC00' && string[j]<='\uD7A3') {
                ko++;
                ko++;
            }
            else {
                etc++;
            }
        }

        return (en + ko + etc);

    }

}
