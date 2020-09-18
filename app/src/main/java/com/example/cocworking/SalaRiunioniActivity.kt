package com.example.cocworking

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocworking.databinding.ActivitySalaRiunioniBinding
import com.example.cocworking.databinding.CalendarDayLayoutBinding
import com.example.cocworking.models.Event
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import kotlinx.android.synthetic.main.activity_sala_riunioni.*
import kotlinx.android.synthetic.main.calendar_day_layout.view.*
import kotlinx.android.synthetic.main.calendar_header.view.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import java.util.stream.Collectors

class SalaRiunioniActivity : AppCompatActivity() {

    private val today = LocalDate.now()
    private var selectedDate: LocalDate? = null
    private var augurimamma = "auguri mamma"
    private var augurinonna = "augurinonna"
    private var augurizia = "augurizia"
    private var oggi = LocalDate.parse("2020-09-17")
    private var domani = LocalDate.parse("2020-09-18")

    private var eventmap = mutableMapOf<LocalDate, List<Event>>()

    var eventi: List<Event> = emptyList()

    val evento : Event = Event(UUID.randomUUID().toString(), augurinonna, oggi)

    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    private lateinit var binding: ActivitySalaRiunioniBinding

    //val e : Map<LocalDate, List<Event>> = eventi.stream().collect(Collectors.groupingBy(Event::date, Collectors.toCollection()))


    private val eventsAdapter = EventAdapter {
        AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
            .setMessage(R.string.event_dialog_delete)
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton(R.string.close, null)
            .show()
    }

    private val inputDialog by lazy {
        val editText = AppCompatEditText(this)
        val layout = FrameLayout(this).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, context)
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }



        AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert)
            .setTitle(getString(R.string.event_input_dialog_title))
            .setView(layout)
            .setPositiveButton(R.string.save) { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton(R.string.close, null)
            .create()
            .apply {
                setOnShowListener {
                    // Show the keyboard
                    editText.requestFocus()
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                setOnDismissListener {
                    // Hide the keyboard
                    context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
                }
            }
    }


    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(this, R.string.empty_input_text, Toast.LENGTH_LONG).show()
        } else {
            selectedDate?.let {
                eventmap[it] = eventmap[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                Log.d(text, "testo evento")
                Log.d(it.toString(), "data evento")
                //Log.d(oggi.toString(), "data odierna")
                updateAdapterForDate(it)
            }
        }
    }


    private fun deleteEvent(event: Event) {
        val date = event.date
        eventmap[date] = eventmap[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            /*if(eventi[date]?.size != 0){
                events.addAll(this@SalaRiunioniActivity.eventi[date].orEmpty())
                eventi.clear();
            }*/
            //Log.d(this@SalaRiunioniActivity.events[date]?.single().toString(), "dimensione lista")
            //this@SalaRiunioniActivity.events[date]?.plus(eventi[oggi].orEmpty().plus(Event(UUID.randomUUID().toString(), augurinonna, oggi)))
            this@SalaRiunioniActivity.eventmap[date]?.stream()
                ?.distinct()
                ?.collect(Collectors.toList())
            Log.d(this@SalaRiunioniActivity.eventmap[date]?.size.toString(), "dimensione lista")
            events.addAll(this@SalaRiunioniActivity.eventmap[date].orEmpty())
            notifyDataSetChanged()
        }
        selectedDateText?.text = selectionFormatter.format(date)
    }

    private fun updateAdapterForCalendar(date: LocalDate) {
        eventsAdapter.apply {
            events.clear()
            //events.addAll(this@SalaRiunioniActivity.events[date].orEmpty())
            //events.addAll(this@SalaRiunioniActivity.eventi[date].orEmpty())
            notifyDataSetChanged()
        }
        selectedDateText?.text = selectionFormatter.format(date)
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { calendarView?.notifyDateChanged(it) }
            calendarView?.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun initEventRecyclerView(){
        eventsRv?.apply{
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = eventsAdapter
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sala_riunioni)
        setSupportActionBar(findViewById(R.id.toolbar_orange))


        val binding = ActivitySalaRiunioniBinding.inflate(layoutInflater)

        initEventRecyclerView()

        if (savedInstanceState == null) {
            calendarView?.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarDayText
            val dotView = view.DotView
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayLayoutBinding.inflate(layoutInflater)

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                    selectDate(day.date)
                    }
                }
            }
        }
/*
        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarDayText

            lateinit var day: CalendarDay // Will be set when this container is bound.
            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }

            // Without the kotlin android extensions plugin:
            //val textView = view.findViewById<TextView>(R.id.calendarDayText)
        }
*/

        calendarView?.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.colorPrimary)
                            textView.setBackgroundResource(R.drawable.today_bg)
                            dotView.makeInVisible()
                            Log.d("day","1")
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.colorBase2)
                            textView.setBackgroundResource(R.drawable.selected_bg)
                            dotView.makeInVisible()
                            Log.d("day","2")
                        }
                        else -> {
                            textView.setTextColorRes(R.color.colorPrimaryDark)
                            textView.background = null
                            if(eventmap[day.date].orEmpty().isNotEmpty()) {
                                dotView.makeInVisible()
                                Log.d("day","3")
                            }
                            else{dotView.makeInVisible()
                                Log.d("day","4")}
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

/*
        calendarView?.dayBinder = object :DayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.textView.text = day.date.dayOfMonth.toString()
            }
        }
*/
        class MonthViewContainer(view: View) : ViewContainer(view) {
            val textView = view.calendarHeaderText
        }
        calendarView?.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                val monthTitle = "${month.yearMonth.month.name.toLowerCase().capitalize()} ${month.year}"
                container.textView.text = monthTitle
            }
        }


        eventi.toMutableList().add(evento)
        eventi = eventi.orEmpty().plusElement(Event(UUID.randomUUID().toString(), augurimamma, oggi))
        eventi.plusElement(Event(UUID.randomUUID().toString(), augurizia, domani))

        Log.d(eventi.size.toString(), "dimensione lista eventi")

        eventmap = eventi.groupBy { it.date }.toMutableMap()
        Log.d(eventmap.size.toString(), "dimensione mappa")

        //val e : Map<LocalDate, List<Event>> = eventi.stream().collect(Collectors.groupingBy(Event::date, Collectors.toCollection()))
        //val events : Map<LocalDate, List<Event>> = eventi.groupBy { it.date }

        /*events[oggi] = events[oggi].orEmpty().plus(Event(UUID.randomUUID().toString(), augurinonna, oggi))
        events[oggi] = events[oggi].orEmpty().plus(Event(UUID.randomUUID().toString(), augurimamma, oggi))
        events[domani] = events[domani].orEmpty().plus(Event(UUID.randomUUID().toString(), augurizia, domani))*/


        AddButton?.setOnClickListener {
            /*if(events[oggi]?.size != 0){
                updateAdapterForDate(oggi)
                events.clear();
            }
            events[oggi] = events[oggi].orEmpty().plus(Event(UUID.randomUUID().toString(), auguri, oggi))
            updateAdapterForDate(oggi)*/
            inputDialog.show()
        }

        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        calendarView?.setup(firstMonth, lastMonth, firstDayOfWeek)
        calendarView?.scrollToMonth(currentMonth)
        Log.d("CALENDAR_CREATION","$currentMonth \n $firstMonth \n $lastMonth \n $firstDayOfWeek")
    }

    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    //Funzioni che si attivano quando viene cliccato un elemento della barra
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.setting -> {
            // User chose the "Settings" item, show the app settings UI...
            val setting = Intent(applicationContext,SetupActivity::class.java)
            startActivity(setting)
            true
        }
        R.id.account -> {
            //Se clicco su "profilo" si apre l'activity relativa
            val profilo = Intent(applicationContext,ProfiloActivity::class.java)
            startActivity(profilo)
            true
        }
        R.id.allarm -> {
            // User chose the "Allarm" action for see notifications
            val notifiche = Intent(applicationContext,NotificheActivity::class.java)
            startActivity(notifiche)
            true
        }
        R.id.home -> {
            // uso "home" action per aprire MainActivity
            val home = Intent(applicationContext,MainActivity::class.java)
            startActivity(home)
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
