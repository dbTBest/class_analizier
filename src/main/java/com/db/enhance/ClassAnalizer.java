package com.db.enhance;

import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Scope("singleton")
public class ClassAnalizer {
    @RequestMapping(value = "/execute", method = RequestMethod.POST, produces = "application/json;charset=utf-8;")
    public void addAlarm(@RequestBody String param) {
        A a = new A();
        a.a();
        System.out.println("进入控制类并被打印");
        BufferedInputStream is = new BufferedInputStream(A.class.getResourceAsStream("/com/db/enhance/A.class"));
        try {
            int ibn = is.available();
            byte[] cb = new byte[ibn];
            System.out.println(ibn);
            is.read(cb, 0, ibn);
            getMagicNumber(cb);
            getJDKVersion(cb);
            int offset=getConstantPoolInfo(cb);
            getAccessTags(cb,offset);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getAccessTags(byte[] cb,int offset){
        int sum=0;
        for(int i=0;i<2;i++){
            sum+=(cb[offset+i]<<24)>>>(24-(1-i)*8);
        }
        System.out.println("访问标志为："+Integer.toHexString((short)sum));
    }

    public void getMagicNumber(byte[] cb){
        byte[] mnb = ByteUtil.byte2byte(cb, 0, 4);
        int sum=0;
        for (int i = 0; i < 4; i++) {
            int bi=(mnb[i]<<24)>>>i*8;
            sum+=bi;
        }
        System.out.println("魔数："+Integer.toHexString(sum));
    }

    public void getJDKVersion(byte[] cb){
        int minorV=0;
        for(int i=4;i<6;i++){
            int bi=(cb[i]<<24)>>>(24-(5-i)*8);
            minorV+=bi;
        }
        int majorV=0;
        for(int i=6;i<8;i++){
            int bi=(cb[i]<<24)>>>(24-(7-i)*8);
            majorV+=bi;
        }
        System.out.println("jdk版本为1."+(majorV-45+1)+"；次版本号为"+minorV);
    }

    private static byte oneB=0x7f;
    private static byte twoB= (byte) 0xdf;
    private static byte threeB= (byte) 0xef;
    public int getConstantPoolInfo(byte[] cb){
        int cpn=0;
        for(int i=8;i<10;i++){
            int bi=(cb[i]<<24)>>>(24-(9-i)*8);
            cpn+=bi;
        }
        System.out.println("常量池有"+(cpn=cpn-1)+"项常量");
        int offset=10;
        for(int i=0;i<cpn;i++){
            byte tb=cb[offset];
            if(tb==1){
                System.out.println("第"+(i+1)+"项为CONSTANT_Utf8_info类型");
                int leng=0;
                for(int j=1;j<=2;j++){
                    int bj=(cb[offset+j]<<24)>>>(24-(2-j)*8);
                    leng+=bj;
                }
                offset+=3;
                System.out.println("所占字节长度为"+leng);
                StringBuilder sb=new StringBuilder();
                int counter=1;
                while(counter++<=leng){
                    byte b=cb[offset+counter-2];

                    if(((oneB|b)^oneB)==0){
                        sb.append((char) b);
                        continue;
                    }
                    if(((twoB|b)^twoB)==0){
                        byte b1=cb[offset+counter-1];
                        int sum=0;
                        sum+=(b<<24)>>>16;
                        sum+=(b1<<24)>>>24;
                        sb.append((char) sum);
                        counter+=1;
                        continue;
                    }
                    if(((threeB|b)^threeB)==0){
                        byte b1=cb[offset+counter-1];
                        byte b2=cb[offset+counter];
                        int sum=0;
                        sum+=(b<<24)>>>8;
                        sum+=(b1<<24)>>>16;
                        sum+=(b2<<24)>>>24;
                        sb.append((char) sum);
                        counter+=2;
                        continue;
                    }
                }
                System.out.println("符号引用为："+sb);
                offset+=leng;
                continue;
            }
            if(tb==3){
                continue;
            }
            if(tb==4){
                continue;
            }
            if(tb==5){
                continue;
            }
            if(tb==6){
                continue;
            }
            if(tb==7){
                System.out.println("第"+(i+1)+"项为CONSTANT_Class_info类型");
                offset+=1;
                int ci=0;
                for(int cii=0;cii<2;cii++){
                    int bi=(cb[offset+cii]<<24)>>>(24-8*(1-cii));
                    ci+=bi;
                }
                System.out.println("CONSTANT_Utf8_info索引为："+ci);
                offset+=2;
                continue;
            }
            if(tb==8){
                continue;
            }
            if(tb==9){
                continue;
            }
            if(tb==10){
                System.out.println("第"+(i+1)+"项为CONSTANT_Methodref_info类型");
                offset+=1;
                int cii=0;
                for(int mrii=0;mrii<2;mrii++){
                    int bi=(cb[offset+mrii]<<24)>>>(24-8*(1-mrii));
                    cii+=bi;
                }
                System.out.println("类描述符索引为："+cii);
                offset+=2;
                int nti=0;
                for(int mrii=0;mrii<2;mrii++){
                    int bi=(cb[offset+mrii]<<24)>>>(24-8*(1-mrii));
                    nti+=bi;
                }
                System.out.println("名称及类型描述符索引为："+nti);
                offset+=2;
                continue;
            }
            if(tb==11){
                continue;
            }
            if(tb==12){
                System.out.println("第"+(i+1)+"项为CONSTANT_NameAndType_info类型");
                offset+=1;
                int ni=0;
                for(int nii=0;nii<2;nii++){
                    int bi=(cb[offset+nii]<<24)>>>(24-8*(1-nii));
                    ni+=bi;
                }
                System.out.println("字段或方法名称索引为："+ni);
                offset+=2;
                int ti=0;
                for(int tii=0;tii<2;tii++){
                    int bi=(cb[offset+tii]<<24)>>>(24-8*(1-tii));
                    ti+=bi;
                }
                System.out.println("字段或方法描述符索引为："+ti);
                offset+=2;
                continue;
            }
            if(tb==15){
                continue;
            }
            if(tb==16){
                continue;
            }
            if(tb==18){
                continue;
            }
        }
        return offset;
    }
}
