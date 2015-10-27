package com.open.ui.google_recyclerview;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ItemAnimator;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;

public class MyItemAnimator extends ItemAnimator {

    List<RecyclerView.ViewHolder> mAnimationAddViewHolders = new ArrayList<RecyclerView.ViewHolder>();
    List<RecyclerView.ViewHolder> mAnimationRemoveViewHolders = new ArrayList<RecyclerView.ViewHolder>();

    // 需要执行动画时会系统会调用，用户无需手动调用
    @Override
    public void runPendingAnimations() {
        if (!mAnimationAddViewHolders.isEmpty()) {
            for (final RecyclerView.ViewHolder viewHolder : mAnimationAddViewHolders) {
                View target = viewHolder.itemView;
                AnimatorSet animator = new AnimatorSet();

                animator.playTogether(
                        ObjectAnimator.ofFloat(target, "translationX",
                                -target.getMeasuredWidth(), 0.0f),
                        ObjectAnimator.ofFloat(target, "alpha",
                                target.getAlpha(), 1.0f));

                animator.setTarget(target);
                animator.setDuration(100);
                animator.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mAnimationAddViewHolders.remove(viewHolder);
                        if (!isRunning()) {
                            dispatchAnimationsFinished();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub
                        
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub
                        
                    }
                });
                animator.start();
            }
        } else if (!mAnimationRemoveViewHolders.isEmpty()) {
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {
    }

    @Override
    public void endAnimations() {
    }

    @Override
    public boolean isRunning() {
        return !(mAnimationAddViewHolders.isEmpty() && mAnimationRemoveViewHolders.isEmpty());
    }

    @Override
    public boolean animateAdd(ViewHolder viewHolder) {
        return false;
    }

    @Override
    public boolean animateMove(ViewHolder viewHolder, int arg1, int arg2, int arg3, int arg4) {
        return mAnimationAddViewHolders.add(viewHolder);
    }

    @Override
    public boolean animateRemove(ViewHolder viewHolder) {
        return mAnimationRemoveViewHolders.add(viewHolder);
    }

}
