package com.example.tacos


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object{const val REQUEST_IMAGE_CAPTURE = 1 }
    private var count = 1
    private val textList: MutableList<String> = ArrayList()

    private val tacoHashMap: HashMap<String, tacoType> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        deleteButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
        add.setOnClickListener {
            var fileContents = editText.text.toString()
            // reset editText
            editText.text = null

            textList.add(fileContents)

            // Create a dummy image
            val w: Int = 1
            val h: Int = 1
            val conf = Bitmap.Config.ARGB_8888 // see other conf types
            val bmp = Bitmap.createBitmap(w, h, conf) // this creates a MUTABLE bitmap
            tacoHashMap[fileContents]=tacoType(bmp, false)

            scrollChoice.addItems(textList,2)

            // Reset the content
            imageView.setImageResource(0)
            giveMeTaco.text = fileContents
        }
        scrollChoice.setOnItemSelectedListener { scrollChoice, position, name ->
            imageView.setImageResource(0)
            giveMeTaco.text = ""

            if (tacoHashMap[name]!!.isImage == false) {
                giveMeTaco.text = name
            } else {
                imageView.setImageBitmap(tacoHashMap[name]!!.image)
            }
        }
    }

    private fun dispatchTakePictureIntent(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also{
            takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also{
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if( requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Concatenate a image name
            var imgNameHead:String = "Image Memo"
            var countString = count.toString()
            var imgName:String = "$imgNameHead $countString"
            tacoHashMap[imgName] = tacoType(imageBitmap, true)
            textList.add(imgName)
            scrollChoice.addItems(textList,2)
            count++

            // Reset the content
            giveMeTaco.text = ""
            imageView.setImageBitmap(imageBitmap)
        }
    }

}


