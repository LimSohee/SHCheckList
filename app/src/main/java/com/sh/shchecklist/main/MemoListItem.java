package com.sh.shchecklist.main;

public class MemoListItem {
    private int mMemoId;
    private String mMemoTitle;
    private int mTotalCount;
    private int mCheckCount;
    private String mCreateDate;
    private int mPriority;

    public MemoListItem(int memoId, String title, int totalCount, int checkCount, String createDate, int priority){
        this.mMemoId = memoId;
        this.mMemoTitle = title;
        this.mTotalCount = totalCount;
        this.mCheckCount = checkCount;
        this.mCreateDate = createDate;
        this.mPriority = priority;
    }

    public int getMemoId() {
        return mMemoId;
    }

    public void setMemoId(int memoId) {
        this.mMemoId = memoId;
    }

    public String getMemoTitle() {
        return mMemoTitle;
    }

    public void setMemoTitle(String memotitle) {
        this.mMemoTitle = memotitle;
    }

    public int getTotalCount() {
        return mTotalCount;
    }

    public void setTotalCount(int totalCount) {
        this.mTotalCount = totalCount;
    }

    public int getCheckCount() {
        return mCheckCount;
    }

    public void setCheckCount(int checkCount) {
        this.mCheckCount = checkCount;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String createDate) {
        this.mCreateDate = createDate;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }
}
