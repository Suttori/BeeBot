package com.suttori.demobottty3.entity;

import org.springframework.stereotype.Component;

@Component
public class FlagController {

    private boolean takeChannelFlag;
    private boolean isNewPost;
    private boolean postPublished;
    private boolean addMedia;
    private boolean addText;
    private boolean postCanceled;

    public boolean isPostCanceled() {
        return postCanceled;
    }

    public void setPostCanceled(boolean postCanceled) {
        this.postCanceled = postCanceled;
    }

    public boolean isAddText() {
        return addText;
    }

    public void setAddText(boolean addText) {
        this.addText = addText;
    }

    public boolean isAddMedia() {
        return addMedia;
    }

    public void setAddMedia(boolean addMedia) {
        this.addMedia = addMedia;
    }

    public boolean isTakeChannelFlag() {
        return takeChannelFlag;
    }

    public void setTakeChannelFlag(boolean takeChannelFlag) {
        this.takeChannelFlag = takeChannelFlag;
    }

    public boolean isNewPost() {
        return isNewPost;
    }

    public void setNewPost(boolean newPost) {
        isNewPost = newPost;
    }

    public boolean isPostPublished() {
        return postPublished;
    }

    public void setPostPublished(boolean postPublished) {
        this.postPublished = postPublished;
    }
}
