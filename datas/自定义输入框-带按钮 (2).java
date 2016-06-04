/**
 * @类描述:带清楚按钮的editText输入框
 * @创建时间:2015年2月7日-上午11:25:08
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @版本:
 */
public class MyEditText extends EditText implements  
OnFocusChangeListener, TextWatcher { 
/**
     * @说明:清空按钮显示图标
     * @名称:mClearDrawable
     * @类型:Drawable
     */   
private Drawable mClearDrawable; 
/**
     * @说明:控件是否获得焦点
     * @名称:hasFoucs
     * @类型:boolean
     * */
    private boolean hasFoucs;
public MyEditText (Context context) { 
this(context, null); 
    } 
public MyEditText (Context context, AttributeSet attrs) { 
/**注意这里android.R.attr.editTextStyle **/
        this(context, attrs, android.R.attr.editTextStyle); 
    } 
public MyEditText (Context context, AttributeSet attrs, int defStyle) {
super(context, attrs, defStyle);
        init();
    }
/**
     * @方法说明:初始化控件数据
     * @方法名称:init
     * @返回值:void
     */
    private void init() { 
/**获取EditText的DrawableRight,假如没有设置我们就使用默认的图片**/
        mClearDrawable = getCompoundDrawables()[2]; 
if (mClearDrawable == null) { 
/** throw new NullPointerException("You can add drawableRight attribute in XML");**/
            mClearDrawable = getResources().getDrawable(R.drawable.delete_selector); 
        } 
        mClearDrawable.setBounds(
0, 0, 
                        mClearDrawable.getIntrinsicWidth(), 
                        mClearDrawable.getIntrinsicHeight()); 
/**默认设置隐藏图标**/
        setClearIconVisible(false); 
/**设置焦点改变的监听**/
        setOnFocusChangeListener(this); 
/**设置输入框里面内容发生改变的监听**/
        addTextChangedListener(this); 
    } 


/**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @Override 
public boolean onTouchEvent(MotionEvent event) {
if (event.getAction() == MotionEvent.ACTION_UP) {
if (getCompoundDrawables()[2] != null) {
boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
if (touchable) {
this.setText("");
                }
            }
        }
return super.onTouchEvent(event);
    }


    /**
     * @重写方法名:onFocusChange
     * @父类:@see android.view.View.OnFocusChangeListener#onFocusChange(android.view.View, boolean)
     * @方法说明:当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     **/
    @Override 
    public void onFocusChange(View v, boolean hasFocus) { 
        this.hasFoucs = hasFocus;
        if (hasFocus) { 
            setClearIconVisible(getText().length() > 0); 
        } else { 
            setClearIconVisible(false); 
        } 
    } 



/**
     * @方法说明:设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @方法名称:setClearIconVisible
     * @param visible
     * @返回值:void
     */  protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], 
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 



/**
     * @重写方法名:onTextChanged
     * @父类:@see android.widget.TextView#onTextChanged(java.lang.CharSequence, int, int, int)
     * @方法说明:主要监控输入框，当存在内容时候，显示删除的按钮     */  
@Override 
public void onTextChanged(CharSequence s, int start, int count, 
int after) { 
if(hasFoucs){
                    setClearIconVisible(s.length() > 0);
                }
    }




@Override 
public void beforeTextChanged(CharSequence s, int start, int count, 
int after) { 
    } 

    
@Override 
public void afterTextChanged(Editable s) { 
    } 
/**
     * @方法说明:设置摇换动画
     * @方法名称:setShakeAnimation
     * @返回值:void
     **/
    public void setShakeAnimation(){
this.setAnimation(shakeAnimation(5));
    }
/**
     * @方法说明:摇换的动画
     * @方法名称:shakeAnimation
     * @param counts一秒钟摇换多少次
     * @return
     * @返回值:Animation
     */ 
public static Animation shakeAnimation(int counts){
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnima
