package com.sh.shchecklist.check;

public class CheckListItem {

    private int mMemoId;
    private int mCheckId;
    private String mCheckBoxText;
    private String mDescription;
    private int mIsChecked;

    public CheckListItem(int memoId, int checkId, String checkboxText, String description, int isChecked){
        this.mMemoId = memoId;
        this.mCheckId = checkId;
        this.mCheckBoxText = checkboxText;
        this.mDescription = description;
        this.mIsChecked = isChecked;
    }

    public int getCheckId() {
        return mCheckId;
    }

    public void setCheckId(int checkId) {
        this.mCheckId = checkId;
    }

    public String getCheckBoxText() {
        return mCheckBoxText;
    }

    public void setCheckBoxText(String checkboxText) {
        this.mCheckBoxText = checkboxText;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getIsChecked() {
        return mIsChecked;
    }

    public void setIsChecked(int isChecked) {
        if(isChecked == 0)
            this.mIsChecked = 0;
        else
            this.mIsChecked = 1;
    }

    public int getMemoId() {
        return mMemoId;
    }

    public void setMemoId(int memoId) {
        mMemoId = memoId;
    }
}
