package com.example.huacaolu.bean;

import java.util.List;

public class SearchWordsPlantBean {
    private int status;
    private List<MsgDTO> msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<MsgDTO> getMsg() {
        return msg;
    }

    public void setMsg(List<MsgDTO> msg) {
        this.msg = msg;
    }

    public static class MsgDTO {

        String _$Type258;// FIXME check this code
        private ResultDTO result;
        private double resultScore;

        public String get_$Type258() {
            return _$Type258;
        }

        public void set_$Type258(String _$Type258) {
            this._$Type258 = _$Type258;
        }

        public ResultDTO getResult() {
            return result;
        }

        public void setResult(ResultDTO result) {
            this.result = result;
        }

        public double getResultScore() {
            return resultScore;
        }

        public void setResultScore(double resultScore) {
            this.resultScore = resultScore;
        }

        public static class ResultDTO {

            List<String> _$Type179;// FIXME check this code
            private String name;
            private String description;
            private DetailedDescriptionDTO detailedDescription;
            private ImageDTO image;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> get_$Type179() {
                return _$Type179;
            }

            public void set_$Type179(List<String> _$Type179) {
                this._$Type179 = _$Type179;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public DetailedDescriptionDTO getDetailedDescription() {
                return detailedDescription;
            }

            public void setDetailedDescription(DetailedDescriptionDTO detailedDescription) {
                this.detailedDescription = detailedDescription;
            }

            public ImageDTO getImage() {
                return image;
            }

            public void setImage(ImageDTO image) {
                this.image = image;
            }

            public static class DetailedDescriptionDTO {
                private String articleBody;
                private String url;

                public String getArticleBody() {
                    return articleBody;
                }

                public void setArticleBody(String articleBody) {
                    this.articleBody = articleBody;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }

            public static class ImageDTO {
                private String contentUrl;
                private String url;

                public String getContentUrl() {
                    return contentUrl;
                }

                public void setContentUrl(String contentUrl) {
                    this.contentUrl = contentUrl;
                }

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
