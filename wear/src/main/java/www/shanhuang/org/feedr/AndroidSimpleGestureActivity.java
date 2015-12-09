package www.shanhuang.org.feedr;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;

public class AndroidSimpleGestureActivity extends Activity {

 TextView gestureEvent;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_suggestion);
//       gestureEvent = (TextView)findViewById(R.id.GestureEvent);
       Log.d("TAG", "gesture event is created");
   }

   @Override
 public boolean onTouchEvent(MotionEvent event) {
  // TODO Auto-generated method stub
       Log.d("touch event", "Touch event has started");
    return gestureDetector.onTouchEvent(event);
 }

 SimpleOnGestureListener simpleOnGestureListener
   = new SimpleOnGestureListener(){


  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
    float velocityY) {
   String swipe = "";
   float sensitivity = 50;
      Log.d("this", "starting on fling method");

   // TODO Auto-generated method stub
//   if((e1.getX() - e2.getX()) > sensitivity){
//       Log.d("swipeleft", "detecting leftswipe");
//    swipe += "Swipe Left\n";
//   }else if((e2.getX() - e1.getX()) > sensitivity){
//    swipe += "Swipe Right\n";
//   }else{
//    swipe += "\n";
//   }

   if((e1.getY() - e2.getY()) > sensitivity){
    swipe += "Swipe Up\n";
   }else if((e2.getY() - e1.getY()) > sensitivity){
    swipe += "Swipe Down\n";
   }else{
    swipe += "\n";
   }

   gestureEvent.setText(swipe);

   return super.onFling(e1, e2, velocityX, velocityY);
  }
   };

   GestureDetector gestureDetector
   = new GestureDetector(simpleOnGestureListener);
}


