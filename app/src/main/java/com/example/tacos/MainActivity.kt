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
    private var count = 0 // Count number of Images
    private lateinit var selectedItemText:String
    private var selectedItemPosition = 0
    private val textList: MutableList<String> = ArrayList()
    private val tacoList: MutableList<TacoType> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        snapButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        add.setOnClickListener {
            var fileContents = editText.text.toString()
            // Reset editText
            editText.text = null

            // Force user cannot add empty item
            if (fileContents!="") {
                // Add to text list
                textList.add(fileContents)

                // Create a dummy image
                val w: Int = 1
                val h: Int = 1
                val conf = Bitmap.Config.ARGB_8888 // see other conf types
                val bmp = Bitmap.createBitmap(w, h, conf) // this creates a MUTABLE bitmap

                // Add to the TacoList
                tacoList.add(TacoType(fileContents, bmp, false))

                // Reset the content
                imageView.setImageResource(0)
                giveMeTaco.text = fileContents
                selectedItemText = fileContents

                // Add text item to the end of the list
                selectedItemPosition = textList.size - 1

                // 3rd part lib: https://github.com/webianks/ScrollChoice
                // MIT licence
                scrollChoice.addItems(textList, selectedItemPosition)
            }
        }

        deleteButton.setOnClickListener {
            if (textList.size > 0 && selectedItemText != "" ) {
                // Remove selected item from both lists
                textList.removeAt(selectedItemPosition)
                tacoList.removeAt(selectedItemPosition)

                // Reset the content
                imageView.setImageResource(0)
                giveMeTaco.text = ""
                if (textList.size > 1 && selectedItemPosition !=0 ) {
                    // This case the selective cursor will move one step up
                    selectedItemPosition--
                    selectedItemText = textList[selectedItemPosition]
                } else if (textList.size == 1) {
                    selectedItemText = textList[0]
                    selectedItemPosition = 0
                } else if (textList.size == 0) {
                    selectedItemText = ""
                    selectedItemPosition = 0
                } else {
                    // textList.size > 1 && selectedItemPosition == 0
                    // Which means now there are more then one item in the spinner
                    // and the selective cursor is on the top
                    // Then, move down one step after deletion
                    selectedItemText = textList[selectedItemPosition]
                }
                scrollChoice.addItems(textList, selectedItemPosition)

                if (textList.size >= 1) {
                    // Switch the context showing
                    if (tacoList[selectedItemPosition]!!.isImage == false) {
                        giveMeTaco.text = selectedItemText
                    } else {
                        imageView.setImageBitmap(tacoList[selectedItemPosition]!!.image)
                    }
                }
            }
        }

        scrollChoice.setOnItemSelectedListener { scrollChoice, position, name ->
            // Reset the content
            imageView.setImageResource(0)
            giveMeTaco.text = ""
            selectedItemText = name
            selectedItemPosition = position

            // Switch the context showing
            if (tacoList[selectedItemPosition]!!.isImage == false) {
                giveMeTaco.text = name
            } else {
                imageView.setImageBitmap(tacoList[selectedItemPosition]!!.image)
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
            var countString = (count + 1).toString()
            var imgName:String = "$imgNameHead $countString"

            // Add to the HashMap
            tacoList.add(TacoType(imgName, imageBitmap, true))
            textList.add(imgName)

            // Increase the image name number
            count++

            // Reset the content
            giveMeTaco.text = ""
            imageView.setImageBitmap(imageBitmap)
            selectedItemText = imgName

            // Add Image item to the end of the list
            selectedItemPosition = textList.size - 1

            scrollChoice.addItems(textList, selectedItemPosition)
        }
    }
}


