package cardproject.android.arnab.canteen;

import java.io.Serializable;

public class Item implements Serializable {

    private String mtxt1;
    private String[] mfoodname;
    int mquan[],mprice[],mtotal;
    String id="",totalItems="";
    public Item(String txt1,String[] foodname,int[] quan,int[] price)
    {

        mtxt1=txt1;
        mfoodname=foodname;
        mquan=quan;
        mprice=price;
        for(int i=0;i<foodname.length;i++)
            mtotal+=quan[i]*price[i];
    }

    public Item(String id, String totalItems)
    {
        this.id=id;
        this.totalItems=totalItems;

    }

    public String getTotalItems() {
        return totalItems;
    }

    public String getId() {
        return id;
    }

    public String getMtxt1() {
        return mtxt1;
    }

    public String[] getMfoodname() { return mfoodname; }

    public int[] getMquan() { return mquan; }

    public int[] getMprice() { return mprice; }

    public int getMtotal() { return mtotal; }
}
