package com.example.huacaolu.bean;

import java.util.List;

public class PlantBeanJava {

    private long log_id;
    private List<ResultDTO> result;

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public List<ResultDTO> getResult() {
        return result;
    }

    public void setResult(List<ResultDTO> result) {
        this.result = result;
    }

    public static class ResultDTO {
        private double score;
        private String name;
        private BaikeInfoDTO baike_info;

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BaikeInfoDTO getBaike_info() {
            return baike_info;
        }

        public void setBaike_info(BaikeInfoDTO baike_info) {
            this.baike_info = baike_info;
        }

        public static class BaikeInfoDTO {
            private String baike_url;
            private String description;

            public String getBaike_url() {
                return baike_url;
            }

            public void setBaike_url(String baike_url) {
                this.baike_url = baike_url;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }
    }
}
