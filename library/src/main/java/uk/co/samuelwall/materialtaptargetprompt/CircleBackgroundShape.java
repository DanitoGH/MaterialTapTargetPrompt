/*
 * Copyright (C) 2017 Samuel Wall
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.samuelwall.materialtaptargetprompt;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class CircleBackgroundShape implements BackgroundShape
{
    private PointF mPosition;
    private float mRadius;
    private PointF mBasePosition;
    private float mBaseRadius;
    private Paint mPaint;
    private int mBaseColourAlpha;

    public CircleBackgroundShape()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPosition = new PointF();
        mBasePosition = new PointF();
    }


    @Override
    public void setBackgroundColour(int colour)
    {
        mPaint.setColor(colour);
        mBaseColourAlpha = Color.alpha(colour);
        mPaint.setAlpha(mBaseColourAlpha);
    }

    @Override
    public void prepare(final MaterialTapTargetPrompt prompt, final float maxTextWidth)
    {
        /*textWidth = (int) maxTextWidth;
        padding = (int) mTextPadding;*/
        final PointF focalCentre = prompt.getFocalCentre();
        if (prompt.mInside88dpBounds)
        {
            float x1 = focalCentre.x;
            float x2 = prompt.mView.mPrimaryTextLeft - prompt.mTextPadding;
            float y1, y2;
            if (prompt.mVerticalTextPositionAbove)
            {
                y1 = focalCentre.y + prompt.mBaseFocalRadius + prompt.mTextPadding;
                y2 = prompt.mView.mPrimaryTextTop;
            }
            else
            {
                y1 = prompt.mView.mFocalCentre.y - ( prompt.mBaseFocalRadius + prompt.mFocalToTextPadding + prompt.mTextPadding);
                float baseY2 = prompt.mView.mPrimaryTextTop + prompt.mView.mPrimaryTextLayout.getHeight();
                if (prompt.mView.mSecondaryTextLayout != null)
                {
                    baseY2 += prompt.mView.mSecondaryTextLayout.getHeight() + prompt.mView.mTextSeparation;
                }
                y2 = baseY2;
            }

            final float y3 = y2;
            float x3 = x2 + maxTextWidth + prompt.mTextPadding + prompt.mTextPadding;

            final float focalLeft = prompt.mView.mFocalCentre.x - prompt.mBaseFocalRadius - prompt.mFocalToTextPadding;
            final float focalRight = prompt.mView.mFocalCentre.x + prompt.mBaseFocalRadius + prompt.mFocalToTextPadding;
            if (x2 > focalLeft && x2 < focalRight)
            {
                if ( prompt.mVerticalTextPositionAbove)
                {
                    x1 -= prompt.mBaseFocalRadius - prompt.mFocalToTextPadding;
                }
                else
                {
                    x2 -= prompt.mBaseFocalRadius - prompt.mFocalToTextPadding;
                }
            }
            else if (x3 > focalLeft && x3 < focalRight)
            {
                if ( prompt.mVerticalTextPositionAbove)
                {
                    x1 += prompt.mBaseFocalRadius + prompt.mFocalToTextPadding;
                }
                else
                {
                    x3 += prompt.mBaseFocalRadius + prompt.mFocalToTextPadding;
                }
            }

            final double offset = Math.pow(x2, 2) + Math.pow(y2, 2);
            final double bc = (Math.pow(x1, 2) + Math.pow(y1, 2) - offset) / 2.0;
            final double cd = (offset - Math.pow(x3, 2) - Math.pow(y3, 2)) / 2.0;
            final double det = (x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2);
            final double idet = 1 / det;
            mBasePosition.set((float) ((bc * (y2 - y3) - cd * (y1 - y2)) * idet),
                    (float) ((cd * (x1 - x2) - bc * (x2 - x3)) * idet));
            mBaseRadius = (float) Math.sqrt(Math.pow(x2 - mBasePosition.x, 2)
                    + Math.pow(y2 - mBasePosition.y, 2));
            /*point1.set(x1, y1);
            point2.set(x2, y2);
            point3.set(x3, y3);*/
        }
        else
        {
            mBasePosition.set(focalCentre.x, focalCentre.y);
            final float length = Math.abs(prompt.mView.mPrimaryTextLeft
                    + (prompt.mHorizontalTextPositionLeft ? 0 : maxTextWidth)
                    - focalCentre.x) + prompt.mTextPadding;
            float height = prompt.mBaseFocalRadius + prompt.mFocalToTextPadding;
            if (prompt.mView.mPrimaryTextLayout != null)
            {
                height += prompt.mView.mPrimaryTextLayout.getHeight();
            }
            //Check if secondary text should be included with text separation
            if (prompt.mView.mSecondaryTextLayout != null)
            {
                height += prompt.mView.mSecondaryTextLayout.getHeight() + prompt.mView.mTextSeparation;
            }
            mBaseRadius = (float) Math.sqrt(Math.pow(length, 2) + Math.pow(height, 2));
            /*point1.set(prompt.mView.mFocalCentre.x + (mHorizontalTextPositionLeft ? -length : length),
                            prompt.mView.mFocalCentre.y + (mVerticalTextPositionAbove ? - height : height));*/
        }
        mPosition.set(mBasePosition);
    }

    @Override
    public void update(final MaterialTapTargetPrompt prompt, float revealAmount, float alphaModifier)
    {
        final PointF focalCentre = prompt.getFocalCentre();
        mRadius = mBaseRadius * revealAmount;
        mPaint.setAlpha((int) (mBaseColourAlpha * alphaModifier));
        mPosition.set(focalCentre.x + ((mBasePosition.x - focalCentre.x) * revealAmount),
                focalCentre.y + ((mBasePosition.y - focalCentre.y) * revealAmount));
    }

    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawCircle(mPosition.x, mPosition.y, mRadius, mPaint);
    }

    @Override
    public boolean isPointInShape(float x, float y)
    {
        return PromptUtils.isPointInCircle(x, y, mPosition, mRadius);
    }
}
