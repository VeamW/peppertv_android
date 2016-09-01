package com.weilian.phonelive.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by administrato on 2016/8/24.
 */
public class VodBean  {
    /**
     * ret : 200
     * data : {"code":0,"msg":"","info":{"data":[{"id":"948","uid":"948","showid":"1470305083","video_name":"948_1470305083.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180443400.jpeg","is_ban":"0","nums":"5116","starttime":"1465891200","endtime":"1465891370","title":"这些\u201c新美国菜\u201d都没吃过，怎么去美国！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470305083.m4v","date_starttime":"2016-06-14 16:00:00","date_endtime":"2016-06-14 16:02:50","duration":3},{"id":"942","uid":"942","showid":"1470306027","video_name":"942_1470306027.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804182027610.jpeg","is_ban":"0","nums":"4925","starttime":"1470124800","endtime":"1470125019","title":"\u201c它\u201d从斯里兰卡飞来，到底有多狂野！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/upload/avatar/20160803/05235522785856789.png","user_nicename":"小金","video_url":"http://139.224.18.21/public/upload/vod/mv/942_1470306027.m4v","date_starttime":"2016-08-02 16:00:00","date_endtime":"2016-08-02 16:03:39","duration":4},{"id":"948","uid":"948","showid":"1470304852","video_name":"948_1470304852.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180052310.jpeg","is_ban":"0","nums":"3958","starttime":"1470211200","endtime":"1470211346","title":"坐在这里吃下午茶， 分分钟就入戏","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470304852.m4v","date_starttime":"2016-08-03 16:00:00","date_endtime":"2016-08-03 16:02:26","duration":3},{"id":"948","uid":"948","showid":"1470305292","video_name":"948_1470305292.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180812270.jpeg","is_ban":"0","nums":"3713","starttime":"1470884400","endtime":"1470884552","title":"为什么韩国明星来北京都要去这里吃个饭！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470305292.m4v","date_starttime":"2016-08-11 11:00:00","date_endtime":"2016-08-11 11:02:32","duration":3},{"id":"960","uid":"960","showid":"1470305512","video_name":"960_1470305512.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804181152770.jpeg","is_ban":"0","nums":"3214","starttime":"1470877200","endtime":"1470877384","title":"美的人都开始在秘密花园里吃烧烤了！这可怎么得了！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/upload/avatar/20160805/05237155660333379.png","user_nicename":"","video_url":"http://139.224.18.21/public/upload/vod/mv/960_1470305512.m4v","date_starttime":"2016-08-11 09:00:00","date_endtime":"2016-08-11 09:03:04","duration":4}],"last_query_id":"12"}}
     * msg :
     */

    private int ret;
    /**
     * code : 0
     * msg :
     * info : {"data":[{"id":"948","uid":"948","showid":"1470305083","video_name":"948_1470305083.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180443400.jpeg","is_ban":"0","nums":"5116","starttime":"1465891200","endtime":"1465891370","title":"这些\u201c新美国菜\u201d都没吃过，怎么去美国！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470305083.m4v","date_starttime":"2016-06-14 16:00:00","date_endtime":"2016-06-14 16:02:50","duration":3},{"id":"942","uid":"942","showid":"1470306027","video_name":"942_1470306027.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804182027610.jpeg","is_ban":"0","nums":"4925","starttime":"1470124800","endtime":"1470125019","title":"\u201c它\u201d从斯里兰卡飞来，到底有多狂野！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/upload/avatar/20160803/05235522785856789.png","user_nicename":"小金","video_url":"http://139.224.18.21/public/upload/vod/mv/942_1470306027.m4v","date_starttime":"2016-08-02 16:00:00","date_endtime":"2016-08-02 16:03:39","duration":4},{"id":"948","uid":"948","showid":"1470304852","video_name":"948_1470304852.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180052310.jpeg","is_ban":"0","nums":"3958","starttime":"1470211200","endtime":"1470211346","title":"坐在这里吃下午茶， 分分钟就入戏","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470304852.m4v","date_starttime":"2016-08-03 16:00:00","date_endtime":"2016-08-03 16:02:26","duration":3},{"id":"948","uid":"948","showid":"1470305292","video_name":"948_1470305292.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180812270.jpeg","is_ban":"0","nums":"3713","starttime":"1470884400","endtime":"1470884552","title":"为什么韩国明星来北京都要去这里吃个饭！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470305292.m4v","date_starttime":"2016-08-11 11:00:00","date_endtime":"2016-08-11 11:02:32","duration":3},{"id":"960","uid":"960","showid":"1470305512","video_name":"960_1470305512.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804181152770.jpeg","is_ban":"0","nums":"3214","starttime":"1470877200","endtime":"1470877384","title":"美的人都开始在秘密花园里吃烧烤了！这可怎么得了！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/upload/avatar/20160805/05237155660333379.png","user_nicename":"","video_url":"http://139.224.18.21/public/upload/vod/mv/960_1470305512.m4v","date_starttime":"2016-08-11 09:00:00","date_endtime":"2016-08-11 09:03:04","duration":4}],"last_query_id":"12"}
     */

    private DataBean data;
    private String msg;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        private int code;
        private String msg;
        /**
         * data : [{"id":"948","uid":"948","showid":"1470305083","video_name":"948_1470305083.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180443400.jpeg","is_ban":"0","nums":"5116","starttime":"1465891200","endtime":"1465891370","title":"这些\u201c新美国菜\u201d都没吃过，怎么去美国！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470305083.m4v","date_starttime":"2016-06-14 16:00:00","date_endtime":"2016-06-14 16:02:50","duration":3},{"id":"942","uid":"942","showid":"1470306027","video_name":"942_1470306027.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804182027610.jpeg","is_ban":"0","nums":"4925","starttime":"1470124800","endtime":"1470125019","title":"\u201c它\u201d从斯里兰卡飞来，到底有多狂野！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/upload/avatar/20160803/05235522785856789.png","user_nicename":"小金","video_url":"http://139.224.18.21/public/upload/vod/mv/942_1470306027.m4v","date_starttime":"2016-08-02 16:00:00","date_endtime":"2016-08-02 16:03:39","duration":4},{"id":"948","uid":"948","showid":"1470304852","video_name":"948_1470304852.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180052310.jpeg","is_ban":"0","nums":"3958","starttime":"1470211200","endtime":"1470211346","title":"坐在这里吃下午茶， 分分钟就入戏","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470304852.m4v","date_starttime":"2016-08-03 16:00:00","date_endtime":"2016-08-03 16:02:26","duration":3},{"id":"948","uid":"948","showid":"1470305292","video_name":"948_1470305292.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804180812270.jpeg","is_ban":"0","nums":"3713","starttime":"1470884400","endtime":"1470884552","title":"为什么韩国明星来北京都要去这里吃个饭！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg","user_nicename":"祎の❤️❤️❤️","video_url":"http://139.224.18.21/public/upload/vod/mv/948_1470305292.m4v","date_starttime":"2016-08-11 11:00:00","date_endtime":"2016-08-11 11:02:32","duration":3},{"id":"960","uid":"960","showid":"1470305512","video_name":"960_1470305512.m4v","video_pic":"http://139.224.18.21/public/upload/vod/pic/20160804181152770.jpeg","is_ban":"0","nums":"3214","starttime":"1470877200","endtime":"1470877384","title":"美的人都开始在秘密花园里吃烧烤了！这可怎么得了！","address":"","province":"","city":"","light":"0","weight":"0","is_full":"1","islive":"0","isdel":"0","avatar":"http://139.224.18.21/public/upload/avatar/20160805/05237155660333379.png","user_nicename":"","video_url":"http://139.224.18.21/public/upload/vod/mv/960_1470305512.m4v","date_starttime":"2016-08-11 09:00:00","date_endtime":"2016-08-11 09:03:04","duration":4}]
         * last_query_id : 12
         */

        private InfoBean info;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public InfoBean getInfo() {
            return info;
        }

        public void setInfo(InfoBean info) {
            this.info = info;
        }

        public static class InfoBean {
            private String last_query_id;
            /**
             * id : 948
             * uid : 948
             * showid : 1470305083
             * video_name : 948_1470305083.m4v
             * video_pic : http://139.224.18.21/public/upload/vod/pic/20160804180443400.jpeg
             * is_ban : 0
             * nums : 5116
             * starttime : 1465891200
             * endtime : 1465891370
             * title : 这些“新美国菜”都没吃过，怎么去美国！
             * address :
             * province :
             * city :
             * light : 0
             * weight : 0
             * is_full : 1
             * islive : 0
             * isdel : 0
             * avatar : http://139.224.18.21/public/appcmf/data/upload/20160816/57b2bfd5808ba.jpg
             * user_nicename : 祎の❤️❤️❤️
             * video_url : http://139.224.18.21/public/upload/vod/mv/948_1470305083.m4v
             * date_starttime : 2016-06-14 16:00:00
             * date_endtime : 2016-06-14 16:02:50
             * duration : 3
             */

            private List<DataBeanb> data;

            public String getLast_query_id() {
                return last_query_id;
            }

            public void setLast_query_id(String last_query_id) {
                this.last_query_id = last_query_id;
            }

            public List<DataBeanb> getData() {
                return data;
            }

            public void setData(List<DataBeanb> data) {
                this.data = data;
            }

            public static class DataBeanb implements Serializable {
                private String id;
                private String uid;
                private String showid;
                private String video_name;
                private String video_pic;
                private String is_ban;
                private String nums;
                private String starttime;
                private String endtime;
                private String title;
                private String address;
                private String province;
                private String city;
                private String light;
                private String weight;
                private String is_full;
                private String islive;
                private String isdel;
                private String avatar;
                private String user_nicename;
                private String video_url;
                private String date_starttime;
                private String date_endtime;
                private int duration;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public String getShowid() {
                    return showid;
                }

                public void setShowid(String showid) {
                    this.showid = showid;
                }

                public String getVideo_name() {
                    return video_name;
                }

                public void setVideo_name(String video_name) {
                    this.video_name = video_name;
                }

                public String getVideo_pic() {
                    return video_pic;
                }

                public void setVideo_pic(String video_pic) {
                    this.video_pic = video_pic;
                }

                public String getIs_ban() {
                    return is_ban;
                }

                public void setIs_ban(String is_ban) {
                    this.is_ban = is_ban;
                }

                public String getNums() {
                    return nums;
                }

                public void setNums(String nums) {
                    this.nums = nums;
                }

                public String getStarttime() {
                    return starttime;
                }

                public void setStarttime(String starttime) {
                    this.starttime = starttime;
                }

                public String getEndtime() {
                    return endtime;
                }

                public void setEndtime(String endtime) {
                    this.endtime = endtime;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getAddress() {
                    return address;
                }

                public void setAddress(String address) {
                    this.address = address;
                }

                public String getProvince() {
                    return province;
                }

                public void setProvince(String province) {
                    this.province = province;
                }

                public String getCity() {
                    return city;
                }

                public void setCity(String city) {
                    this.city = city;
                }

                public String getLight() {
                    return light;
                }

                public void setLight(String light) {
                    this.light = light;
                }

                public String getWeight() {
                    return weight;
                }

                public void setWeight(String weight) {
                    this.weight = weight;
                }

                public String getIs_full() {
                    return is_full;
                }

                public void setIs_full(String is_full) {
                    this.is_full = is_full;
                }

                public String getIslive() {
                    return islive;
                }

                public void setIslive(String islive) {
                    this.islive = islive;
                }

                public String getIsdel() {
                    return isdel;
                }

                public void setIsdel(String isdel) {
                    this.isdel = isdel;
                }

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }

                public String getUser_nicename() {
                    return user_nicename;
                }

                public void setUser_nicename(String user_nicename) {
                    this.user_nicename = user_nicename;
                }

                public String getVideo_url() {
                    return video_url;
                }

                public void setVideo_url(String video_url) {
                    this.video_url = video_url;
                }

                public String getDate_starttime() {
                    return date_starttime;
                }

                public void setDate_starttime(String date_starttime) {
                    this.date_starttime = date_starttime;
                }

                public String getDate_endtime() {
                    return date_endtime;
                }

                public void setDate_endtime(String date_endtime) {
                    this.date_endtime = date_endtime;
                }

                public int getDuration() {
                    return duration;
                }

                public void setDuration(int duration) {
                    this.duration = duration;
                }
            }
        }
    }
}
