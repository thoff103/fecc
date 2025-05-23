package FECharMaker

import java.awt.Dimension
import java.awt.Color
import scala.swing.Action
import scala.swing.Button
import scala.collection.MapView
import FireEmblemCharacterCreator.draw_images
import FECharMaker.ColorDialog
import java.util.regex.Pattern
import scala.util.Random


object ColorToolbox {

    extension(color: Color) {
        def to_hex_string: String = 
            "#" + Integer.toHexString(color.getRed())
            + Integer.toHexString(color.getGreen())
            + Integer.toHexString(color.getBlue())
    }

    //private val ColorLabelPrefSize = Dimension(50,30)
    //private val ButtonPrefSize = Dimension(ColorLabelPrefSize.width * 3, 50)
    private val ColorLabelPrefSize = Dimension(40,30)
    private val ButtonPrefSize = Dimension(ColorLabelPrefSize.width * 4, 50)


    class ColorShadeButton( csd: ColorSelectDisplay, val red_index: Int ) extends Button with Elem {
        update_shade()
        preferredSize = ColorLabelPrefSize
        action = Action("") {
            ColorDialog.open(csd, Some(red_index))
        }
        val children = Nil  
        def update_shade(): Unit =
            background = csd.shades( red_index )
        
    }

    class ColorSelectDisplay(private val label_str: String, var color: Color
        , val cidxs: ColorIndices, val prefabs: List[Color], val cvpalettes: List[GameColors.ColorValPalette] = GameColors.HairCVP ) extends Elem:
        SelDisp =>

        var shades = auto_shades

        private val button = Button(this.label_str) {
            ColorDialog.open(SelDisp)
        }
        // button.peer.setSize(ButtonPrefSize)
        // button.minimumSize = ButtonPrefSize

        button.xLayoutAlignment = 0.5

        val rand = new scala .util.Random
        private val rnd_colorbtn = Button("R") {
            // Using "prefabs" -TODO: potentially add other colors
            set_auto_palette(prefabs(rand.nextInt(prefabs.length)))
            draw_images()
        }
            

        private val shade_vis = (for
            (red_idx, idx) <- shades.keys.toSeq.sorted.zipWithIndex
            b = ColorShadeButton( SelDisp, red_idx )
        yield
            b.relx = idx * ColorLabelPrefSize.width
            b.rely = ButtonPrefSize.height
            b
        ).toSeq

        
        
        preferredSize = new Dimension( ColorLabelPrefSize.width * (shade_vis.length+1) , ButtonPrefSize.height + ColorLabelPrefSize.height )
        button.preferredSize = Dimension( preferredSize.width, ButtonPrefSize.height)
        rnd_colorbtn.preferredSize = Dimension( ColorLabelPrefSize.width, ColorLabelPrefSize.height)
        //override val children = ElemLiteral(button) +: shade_vis 
        override val children = ElemLiteral(button) +: shade_vis :+ ElemLiteral(rnd_colorbtn, relx=shade_vis.length * ColorLabelPrefSize.width, rely=ButtonPrefSize.height)

        private def auto_shades: MapView[Int, Color] =
            ColorIndices.to_shades( cidxs, color )

        def refresh_shades(): Unit = 
            shade_vis.foreach( _.update_shade() )

        def set_auto_palette(new_color: Color): Unit =
            this.color = new_color
            set_shades( auto_shades )            

        def set_shades( new_shades: MapView[Int, Color], new_base_color: Option[Color] = None ): Unit =
            shades = new_shades
            new_base_color.foreach( nbc => this.color = nbc )
            refresh_shades()

        def savestring: String = 
            color.to_hex_string + "||" + shades.map( (k,v) => k.toString() + "->" + v.to_hex_string ).mkString("||")
        def load_savestring(savestr: String): Unit =
            val pipesplit = savestr.split(Pattern.quote("||"))
            color = Color.decode( pipesplit(0) )
            shades = (for 
                kvs <- pipesplit.slice(1, pipesplit.length)
                kv = kvs.split( Pattern.quote( "->" ) )
                k = kv(0).toInt
                v = Color.decode(kv(1))
            yield ( k -> v )).toMap.view
            refresh_shades()

            
}
