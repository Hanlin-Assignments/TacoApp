/**
 *  File Name: MainActivity.kt
 *  Project Name: TacoApp
 *  Copyright @ Hanlin Hu 2019
 */

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
    private lateinit var selectedItem:String
    private val textList: MutableList<String> = ArrayList()
    private val tacoHashMap: HashMap<String, TacoType> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snapButton.setOnClickListener {
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

            // Add to the HashMap
            tacoHashMap[fileContents]=TacoType(bmp, false)

            // 3rd part lib: https://github.com/webianks/ScrollChoice
            // MIT licence
            scrollChoice.addItems(textList,2)

            // Reset the content
            imageView.setImageResource(0)
            giveMeTaco.text = fileContents
            selectedItem = fileContents
        }

        deleteButton.setOnClickListener {
            textList.remove(selectedItem)
            tacoHashMap.remove(selectedItem)

            // Reset the content
            imageView.setImageResource(0)
            giveMeTaco.text = ""
            scrollChoice.addItems(textList,2)
        }

        scrollChoice.setOnItemSelectedListener { scrollChoice, position, name ->
            // Reset the content
            imageView.setImageResource(0)
            giveMeTaco.text = ""
            selectedItem = name

            // Switch the context showing
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

            // Add to the HashMap
            tacoHashMap[imgName] = TacoType(imageBitmap, true)
            textList.add(imgName)

            // Increase the image name number
            count++

            // Reset the content
            giveMeTaco.text = ""
            imageView.setImageBitmap(imageBitmap)
            selectedItem = imgName

            scrollChoice.addItems(textList,2)
        }
    }

}


