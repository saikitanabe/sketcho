/* ===================================================
 * bootstrap-transition.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#transitions
 * ===================================================
 * Copyright 2012 Twitter, Inc.
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
 * ========================================================== */


!function (jq172) {

  "use strict"; // jshint ;_;


  /* CSS TRANSITION SUPPORT (http://www.modernizr.com/)
   * ======================================================= */

  jq172(function () {

    jq172.support.transition = (function () {

      var transitionEnd = (function () {

        var el = document.createElement('bootstrap')
          , transEndEventNames = {
               'WebkitTransition' : 'webkitTransitionEnd'
            ,  'MozTransition'    : 'transitionend'
            ,  'OTransition'      : 'oTransitionEnd otransitionend'
            ,  'transition'       : 'transitionend'
            }
          , name

        for (name in transEndEventNames){
          if (el.style[name] !== undefined) {
            return transEndEventNames[name]
          }
        }

      }())

      return transitionEnd && {
        end: transitionEnd
      }

    })()

  })

}(window.jq172);
/* =========================================================
 * bootstrap-modal.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#modals
 * =========================================================
 * Copyright 2012 Twitter, Inc.
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
 * ========================================================= */


!function (jq172) {

  "use strict"; // jshint ;_;


 /* MODAL CLASS DEFINITION
  * ====================== */

  var Modal = function (element, options) {
    this.options = options
    this.jq172element = jq172(element)
      .delegate('[data-dismiss="modal"]', 'click.dismiss.modal', jq172.proxy(this.hide, this))
    this.options.remote && this.jq172element.find('.modal-body').load(this.options.remote)
  }

  Modal.prototype = {

      constructor: Modal

    , toggle: function () {
        return this[!this.isShown ? 'show' : 'hide']()
      }

    , show: function () {
        var that = this
          , e = jq172.Event('show')

        this.jq172element.trigger(e)

        if (this.isShown || e.isDefaultPrevented()) return

        this.isShown = true

        this.escape()

        this.backdrop(function () {
          var transition = jq172.support.transition && that.jq172element.hasClass('fade')

          if (!that.jq172element.parent().length) {
            that.jq172element.appendTo(document.body) //don't move modals dom position
          }

          that.jq172element.show()

          if (transition) {
            that.jq172element[0].offsetWidth // force reflow
          }

          that.jq172element
            .addClass('in')
            .attr('aria-hidden', false)

          that.enforceFocus()

          transition ?
            that.jq172element.one(jq172.support.transition.end, function () { that.jq172element.focus().trigger('shown') }) :
            that.jq172element.focus().trigger('shown')

        })
      }

    , hide: function (e) {
        e && e.preventDefault()

        var that = this

        e = jq172.Event('hide')

        this.jq172element.trigger(e)

        if (!this.isShown || e.isDefaultPrevented()) return

        this.isShown = false

        this.escape()

        jq172(document).off('focusin.modal')

        this.jq172element
          .removeClass('in')
          .attr('aria-hidden', true)

        jq172.support.transition && this.jq172element.hasClass('fade') ?
          this.hideWithTransition() :
          this.hideModal()
      }

    , enforceFocus: function () {
        var that = this
        jq172(document).bind('focusin.modal', function (e) {
          if (that.jq172element[0] !== e.target && !that.jq172element.has(e.target).length) {
            that.jq172element.focus()
          }
        })
      }

    , escape: function () {
        var that = this
        if (this.isShown && this.options.keyboard) {
          this.jq172element.bind('keyup.dismiss.modal', function ( e ) {
            e.which == 27 && that.hide()
          })
        } else if (!this.isShown) {
          this.jq172element.off('keyup.dismiss.modal')
        }
      }

    , hideWithTransition: function () {
        var that = this
          , timeout = setTimeout(function () {
              that.jq172element.off(jq172.support.transition.end)
              that.hideModal()
            }, 500)

        this.jq172element.one(jq172.support.transition.end, function () {
          clearTimeout(timeout)
          that.hideModal()
        })
      }

    , hideModal: function () {
        var that = this
        this.jq172element.hide()
        this.backdrop(function () {
          that.removeBackdrop()
          that.jq172element.trigger('hidden')
        })
      }

    , removeBackdrop: function () {
        this.jq172backdrop && this.jq172backdrop.remove()
        this.jq172backdrop = null
      }

    , backdrop: function (callback) {
        var that = this
          , animate = this.jq172element.hasClass('fade') ? 'fade' : ''

        if (this.isShown && this.options.backdrop) {
          var doAnimate = jq172.support.transition && animate

          this.jq172backdrop = jq172('<div class="modal-backdrop ' + animate + '" />')
            .appendTo(document.body)

          this.jq172backdrop.click(
            this.options.backdrop == 'static' ?
              jq172.proxy(this.jq172element[0].focus, this.jq172element[0])
            : jq172.proxy(this.hide, this)
          )

          if (doAnimate) this.jq172backdrop[0].offsetWidth // force reflow

          this.jq172backdrop.addClass('in')

          if (!callback) return

          doAnimate ?
            this.jq172backdrop.one(jq172.support.transition.end, callback) :
            callback()

        } else if (!this.isShown && this.jq172backdrop) {
          this.jq172backdrop.removeClass('in')

          jq172.support.transition && this.jq172element.hasClass('fade')?
            this.jq172backdrop.one(jq172.support.transition.end, callback) :
            callback()

        } else if (callback) {
          callback()
        }
      }
  }


 /* MODAL PLUGIN DEFINITION
  * ======================= */

  var old = jq172.fn.modal

  jq172.fn.modal = function (option) {
    return this.each(function () {
      var jq172this = jq172(this)
        , data = jq172this.data('modal')
        , options = jq172.extend({}, jq172.fn.modal.defaults, jq172this.data(), typeof option == 'object' && option)
      if (!data) jq172this.data('modal', (data = new Modal(this, options)))
      if (typeof option == 'string') data[option]()
      else if (options.show) data.show()
    })
  }

  jq172.fn.modal.defaults = {
      backdrop: true
    , keyboard: true
    , show: true
  }

  jq172.fn.modal.Constructor = Modal


 /* MODAL NO CONFLICT
  * ================= */

  jq172.fn.modal.noConflict = function () {
    jq172.fn.modal = old
    return this
  }


 /* MODAL DATA-API
  * ============== */

  jq172(document).bind('click.modal.data-api', '[data-toggle="modal"]', function (e) {
    var jq172this = jq172(this)
      , href = jq172this.attr('href')
      , jq172target = jq172(jq172this.attr('data-target') || (href && href.replace(/.*(?=#[^\s]+$)/, ''))) //strip for ie7
      , option = jq172target.data('modal') ? 'toggle' : jq172.extend({ remote:!/#/.test(href) && href }, jq172target.data(), jq172this.data())

    e.preventDefault()

    jq172target
      .modal(option)
      .one('hide', function () {
        jq172this.focus()
      })
  })

}(window.jq172);

/* ============================================================
 * bootstrap-dropdown.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#dropdowns
 * ============================================================
 * Copyright 2012 Twitter, Inc.
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
 * ============================================================ */


!function (jq172) {

  "use strict"; // jshint ;_;


 /* DROPDOWN CLASS DEFINITION
  * ========================= */

  var toggle = '[data-toggle=dropdown]'
    , Dropdown = function (element) {
        var jq172el = jq172(element).bind('click.dropdown.data-api', this.toggle)
        jq172('html').bind('click.dropdown.data-api', function () {
          jq172el.parent().removeClass('open')
        })
      }

  Dropdown.prototype = {

    constructor: Dropdown

  , toggle: function (e) {
      var jq172this = jq172(this)
        , jq172parent
        , isActive

      if (jq172this.is('.disabled, :disabled')) return

      jq172parent = getParent(jq172this)

      isActive = jq172parent.hasClass('open')

      clearMenus()

      if (!isActive) {
        if ('ontouchstart' in document.documentElement) {
          // if mobile we we use a backdrop because click events don't delegate
          jq172('<div class="dropdown-backdrop"/>').insertBefore(jq172(this)).bind('click', clearMenus)
        }
        jq172parent.toggleClass('open')
      }

      jq172this.focus()

      return false
    }

  , keydown: function (e) {
      var jq172this
        , jq172items
        , jq172active
        , jq172parent
        , isActive
        , index

      if (!/(38|40|27)/.test(e.keyCode)) return

      jq172this = jq172(this)

      e.preventDefault()
      e.stopPropagation()

      if (jq172this.is('.disabled, :disabled')) return

      jq172parent = getParent(jq172this)

      isActive = jq172parent.hasClass('open')

      if (!isActive || (isActive && e.keyCode == 27)) {
        if (e.which == 27) jq172parent.find(toggle).focus()
        return jq172this.click()
      }

      jq172items = jq172('[role=menu] li:not(.divider):visible a', jq172parent)

      if (!jq172items.length) return

      index = jq172items.index(jq172items.filter(':focus'))

      if (e.keyCode == 38 && index > 0) index--                                        // up
      if (e.keyCode == 40 && index < jq172items.length - 1) index++                        // down
      if (!~index) index = 0

      jq172items
        .eq(index)
        .focus()
    }

  }

  function clearMenus() {
    jq172('.dropdown-backdrop').remove()
    jq172(toggle).each(function () {
      getParent(jq172(this)).removeClass('open')
    })
  }

  function getParent(jq172this) {
    var selector = jq172this.attr('data-target')
      , jq172parent

    if (!selector) {
      selector = jq172this.attr('href')
      selector = selector && /#/.test(selector) && selector.replace(/.*(?=#[^\s]*$)/, '') //strip for ie7
    }

    jq172parent = selector && jq172(selector)

    if (!jq172parent || !jq172parent.length) jq172parent = jq172this.parent()

    return jq172parent
  }


  /* DROPDOWN PLUGIN DEFINITION
   * ========================== */

  var old = jq172.fn.dropdown

  jq172.fn.dropdown = function (option) {
    return this.each(function () {
      var jq172this = jq172(this)
        , data = jq172this.data('dropdown')
      if (!data) jq172this.data('dropdown', (data = new Dropdown(this)))
      if (typeof option == 'string') data[option].call(jq172this)
    })
  }

  jq172.fn.dropdown.Constructor = Dropdown


 /* DROPDOWN NO CONFLICT
  * ==================== */

  jq172.fn.dropdown.noConflict = function () {
    jq172.fn.dropdown = old
    return this
  }


  /* APPLY TO STANDARD DROPDOWN ELEMENTS
   * =================================== */

  jq172(document)
    .bind('click.dropdown.data-api', clearMenus)
    .bind('click.dropdown.data-api', '.dropdown form', function (e) { e.stopPropagation() })
    .bind('click.dropdown.data-api'  , toggle, Dropdown.prototype.toggle)
    .bind('keydown.dropdown.data-api', toggle + ', [role=menu]' , Dropdown.prototype.keydown)

}(window.jq172);

/* ========================================================
 * bootstrap-tab.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#tabs
 * ========================================================
 * Copyright 2012 Twitter, Inc.
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
 * ======================================================== */


!function (jq172) {

  "use strict"; // jshint ;_;


 /* TAB CLASS DEFINITION
  * ==================== */

  var Tab = function (element) {
    this.element = jq172(element)
  }

  Tab.prototype = {

    constructor: Tab

  , show: function () {
      var jq172this = this.element
        , jq172ul = jq172this.closest('ul:not(.dropdown-menu)')
        , selector = jq172this.attr('data-target')
        , previous
        , jq172target
        , e

      if (!selector) {
        selector = jq172this.attr('href')
        selector = selector && selector.replace(/.*(?=#[^\s]*$)/, '') //strip for ie7
      }

      if ( jq172this.parent('li').hasClass('active') ) return

      previous = jq172ul.find('.active:last a')[0]

      e = jq172.Event('show', {
        relatedTarget: previous
      })

      jq172this.trigger(e)

      if (e.isDefaultPrevented()) return

      jq172target = jq172(selector)

      this.activate(jq172this.parent('li'), jq172ul)
      this.activate(jq172target, jq172target.parent(), function () {
        jq172this.trigger({
          type: 'shown'
        , relatedTarget: previous
        })
      })
    }

  , activate: function ( element, container, callback) {
      var jq172active = container.find('> .active')
        , transition = callback
            && jq172.support.transition
            && jq172active.hasClass('fade')

      function next() {
        jq172active
          .removeClass('active')
          .find('> .dropdown-menu > .active')
          .removeClass('active')

        element.addClass('active')

        if (transition) {
          element[0].offsetWidth // reflow for transition
          element.addClass('in')
        } else {
          element.removeClass('fade')
        }

        if ( element.parent('.dropdown-menu') ) {
          element.closest('li.dropdown').addClass('active')
        }

        callback && callback()
      }

      transition ?
        jq172active.one(jq172.support.transition.end, next) :
        next()

      jq172active.removeClass('in')
    }
  }


 /* TAB PLUGIN DEFINITION
  * ===================== */

  var old = jq172.fn.tab

  jq172.fn.tab = function ( option ) {
    return this.each(function () {
      var jq172this = jq172(this)
        , data = jq172this.data('tab')
      if (!data) jq172this.data('tab', (data = new Tab(this)))
      if (typeof option == 'string') data[option]()
    })
  }

  jq172.fn.tab.Constructor = Tab


 /* TAB NO CONFLICT
  * =============== */

  jq172.fn.tab.noConflict = function () {
    jq172.fn.tab = old
    return this
  }


 /* TAB DATA-API
  * ============ */

  jq172(document).bind('click.tab.data-api', '[data-toggle="tab"], [data-toggle="pill"]', function (e) {
    e.preventDefault()
    jq172(this).tab('show')
  })

}(window.jq172);

/* ===========================================================
 * bootstrap-tooltip.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#tooltips
 * Inspired by the original jQuery.tipsy by Jason Frame
 * ===========================================================
 * Copyright 2012 Twitter, Inc.
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
 * ========================================================== */


!function (jq172) {

  "use strict"; // jshint ;_;


 /* TOOLTIP PUBLIC CLASS DEFINITION
  * =============================== */

  var Tooltip = function (name, element, options) {
    this.init(name, 'tooltip', element, options)
  }

  Tooltip.prototype = {

    constructor: Tooltip

  , init: function (name, type, element, options) {
      var eventIn
        , eventOut
        , triggers
        , trigger
        , i

      this.name = name
      this.type = type
      this.jq172element = jq172(element)
      this.options = this.getOptions(options)
      this.enabled = true

      triggers = this.options.trigger.split(' ')

      for (i = triggers.length; i--;) {
        trigger = triggers[i]
        if (trigger == 'click') {
          this.jq172element.bind('click.' + this.type, this.options.selector, jq172.proxy(this.toggle, this))
        } else if (trigger != 'manual') {
          eventIn = trigger == 'hover' ? 'mouseenter' : 'focus'
          eventOut = trigger == 'hover' ? 'mouseleave' : 'blur'
          this.jq172element.bind(eventIn + '.' + this.type, this.options.selector, jq172.proxy(this.enter, this))
          this.jq172element.bind(eventOut + '.' + this.type, this.options.selector, jq172.proxy(this.leave, this))
        }
      }

      this.options.selector ?
        (this._options = jq172.extend({}, this.options, { trigger: 'manual', selector: '' })) :
        this.fixTitle()
    }

  , getOptions: function (options) {
      options = jq172.extend({}, jq172.fn[this.type].defaults, this.jq172element.data(), options)

      if (options.delay && typeof options.delay == 'number') {
        options.delay = {
          show: options.delay
        , hide: options.delay
        }
      }

      return options
    }

  , enter: function (e) {
      var defaults = jq172.fn[this.type].defaults
        , options = {}
        , self

      this._options && jq172.each(this._options, function (key, value) {
        if (defaults[key] != value) options[key] = value
      }, this)

      self = jq172(e.currentTarget)[this.type](options, this.name).data(this.name)

      if (!self.options.delay || !self.options.delay.show) return self.show()

      clearTimeout(this.timeout)
      self.hoverState = 'in'
      this.timeout = setTimeout(function() {
        if (self.hoverState == 'in') self.show()
      }, self.options.delay.show)
    }

  , leave: function (e) {
      var self = jq172(e.currentTarget)[this.type](this._options, this.name).data(this.name)

      if (this.timeout) clearTimeout(this.timeout)
      if (!self.options.delay || !self.options.delay.hide) return self.hide()

      self.hoverState = 'out'
      this.timeout = setTimeout(function() {
        if (self.hoverState == 'out') self.hide()
      }, self.options.delay.hide)
    }

  , show: function () {
      var jq172tip
        , pos
        , actualWidth
        , actualHeight
        , placement
        , tp
        , e = jq172.Event('show')

      if (this.hasContent() && this.enabled) {
        this.jq172element.trigger(e)
        if (e.isDefaultPrevented()) return
        jq172tip = this.tip()
        this.setContent()

        if (this.options.animation) {
          jq172tip.addClass('fade')
        }

        placement = typeof this.options.placement == 'function' ?
          this.options.placement.call(this, jq172tip[0], this.jq172element[0]) :
          this.options.placement

        jq172tip
          .detach()
          .css({ top: 0, left: 0, display: 'block' })

        this.options.container ? jq172tip.appendTo(this.options.container) : jq172tip.insertAfter(this.jq172element)

        pos = this.getPosition()

        actualWidth = jq172tip[0].offsetWidth
        actualHeight = jq172tip[0].offsetHeight

        switch (placement) {
          case 'bottom':
            tp = {top: pos.top + pos.height, left: pos.left + pos.width / 2 - actualWidth / 2}
            break
          case 'top':
            tp = {top: pos.top - actualHeight, left: pos.left + pos.width / 2 - actualWidth / 2}
            break
          case 'left':
            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left - actualWidth}
            break
          case 'right':
            tp = {top: pos.top + pos.height / 2 - actualHeight / 2, left: pos.left + pos.width}
            break
        }

        this.applyPlacement(tp, placement)
        this.jq172element.trigger('shown')
      }
    }

  , applyPlacement: function(offset, placement){
      var jq172tip = this.tip()
        , width = jq172tip[0].offsetWidth
        , height = jq172tip[0].offsetHeight
        , actualWidth
        , actualHeight
        , delta
        , replace

      jq172tip
        .offset(offset)
        .addClass(placement)
        .addClass('in')

      actualWidth = jq172tip[0].offsetWidth
      actualHeight = jq172tip[0].offsetHeight

      if (placement == 'top' && actualHeight != height) {
        offset.top = offset.top + height - actualHeight
        replace = true
      }

      if (placement == 'bottom' || placement == 'top') {
        delta = 0

        if (offset.left < 0){
          delta = offset.left * -2
          offset.left = 0
          jq172tip.offset(offset)
          actualWidth = jq172tip[0].offsetWidth
          actualHeight = jq172tip[0].offsetHeight
        }

        this.replaceArrow(delta - width + actualWidth, actualWidth, 'left')
      } else {
        this.replaceArrow(actualHeight - height, actualHeight, 'top')
      }

      if (replace) jq172tip.offset(offset)
    }

  , replaceArrow: function(delta, dimension, position){
      this
        .arrow()
        .css(position, delta ? (50 * (1 - delta / dimension) + "%") : '')
    }

  , setContent: function () {
      var jq172tip = this.tip()
        , title = this.getTitle()

      jq172tip.find('.tooltip-inner')[this.options.html ? 'html' : 'text'](title)
      jq172tip.removeClass('fade in top bottom left right')
    }

  , hide: function () {
      var that = this
        , jq172tip = this.tip()
        , e = jq172.Event('hide')

      this.jq172element.trigger(e)
      if (e.isDefaultPrevented()) return

      jq172tip.removeClass('in')

      function removeWithAnimation() {
        var timeout = setTimeout(function () {
          jq172tip.off(jq172.support.transition.end).detach()
        }, 500)

        jq172tip.one(jq172.support.transition.end, function () {
          clearTimeout(timeout)
          jq172tip.detach()
        })
      }

      jq172.support.transition && this.jq172tip.hasClass('fade') ?
        removeWithAnimation() :
        jq172tip.detach()

      this.jq172element.trigger('hidden')

      return this
    }

  , fixTitle: function () {
      var jq172e = this.jq172element
      if (jq172e.attr('title') || typeof(jq172e.attr('data-original-title')) != 'string') {
        jq172e.attr('data-original-title', jq172e.attr('title') || '').attr('title', '')
      }
    }

  , hasContent: function () {
      return this.getTitle()
    }

  , getPosition: function () {
      var el = this.jq172element[0]
      return jq172.extend({}, (typeof el.getBoundingClientRect == 'function') ? el.getBoundingClientRect() : {
        width: el.offsetWidth
      , height: el.offsetHeight
      }, this.jq172element.offset())
    }

  , getTitle: function () {
      var title
        , jq172e = this.jq172element
        , o = this.options

      title = jq172e.attr('data-original-title')
        || (typeof o.title == 'function' ? o.title.call(jq172e[0]) :  o.title)

      return title
    }

  , tip: function () {
      return this.jq172tip = this.jq172tip || jq172(this.options.template)
    }

  , arrow: function(){
      return this.jq172arrow = this.jq172arrow || this.tip().find(".tooltip-arrow")
    }

  , validate: function () {
      if (!this.jq172element[0].parentNode) {
        this.hide()
        this.jq172element = null
        this.options = null
      }
    }

  , enable: function () {
      this.enabled = true
    }

  , disable: function () {
      this.enabled = false
    }

  , toggleEnabled: function () {
      this.enabled = !this.enabled
    }

  , toggle: function (e) {
      var self = e ? jq172(e.currentTarget)[this.type](this._options).data(this.name) : this
      self.tip().hasClass('in') ? self.hide() : self.show()
    }

  , destroy: function () {
      this.hide().jq172element.off('.' + this.type).removeData(this.type)
    }

  }


 /* TOOLTIP PLUGIN DEFINITION
  * ========================= */

  var old = jq172.fn.tooltip

  jq172.fn.tooltip = function ( option, name ) {
    return this.each(function () {
      var jq172this = jq172(this)
        , tooltipName = (name) ? name : 'defaultTooltip'
        , data = jq172this.data(tooltipName)
        , options = typeof option == 'object' && option
      if (!data) jq172this.data(tooltipName, (data = new Tooltip(tooltipName, this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  jq172.fn.tooltip.Constructor = Tooltip

  jq172.fn.tooltip.defaults = {
    animation: true
  , placement: 'top'
  , selector: false
  , template: '<div class="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>'
  , trigger: 'hover focus'
  , title: ''
  , delay: 0
  , html: false
  , container: false
  }


 /* TOOLTIP NO CONFLICT
  * =================== */

  jq172.fn.tooltip.noConflict = function () {
    jq172.fn.tooltip = old
    return this
  }

}(window.jq172);

/* ===========================================================
 * bootstrap-popover.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#popovers
 * ===========================================================
 * Copyright 2012 Twitter, Inc.
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
 * =========================================================== */


!function (jq172) {

  "use strict"; // jshint ;_;


 /* POPOVER PUBLIC CLASS DEFINITION
  * =============================== */

  var Popover = function (name, element, options) {
    this.init(name, 'popover', element, options)
  }


  /* NOTE: POPOVER EXTENDS BOOTSTRAP-TOOLTIP.js
     ========================================== */

  Popover.prototype = jq172.extend({}, jq172.fn.tooltip.Constructor.prototype, {

    constructor: Popover

  , setContent: function () {
      var jq172tip = this.tip()
        , title = this.getTitle()
        , content = this.getContent()

      jq172tip.find('.popover-title')[this.options.html ? 'html' : 'text'](title)
      jq172tip.find('.popover-content')[this.options.html ? 'html' : 'text'](content)

      jq172tip.removeClass('fade top bottom left right in')
    }

  , hasContent: function () {
      return this.getTitle() || this.getContent()
    }

  , getContent: function () {
      var content
        , jq172e = this.jq172element
        , o = this.options

      content = (typeof o.content == 'function' ? o.content.call(jq172e[0]) :  o.content)
        || jq172e.attr('data-content')

      return content
    }

  , tip: function () {
      if (!this.jq172tip) {
        this.jq172tip = jq172(this.options.template)
      }
      return this.jq172tip
    }

  , destroy: function () {
      this.hide().jq172element.off('.' + this.type).removeData(this.type)
    }

  })


 /* POPOVER PLUGIN DEFINITION
  * ======================= */

  var old = jq172.fn.popover

  jq172.fn.popover = function (option, name) {
    return this.each(function () {
      var jq172this = jq172(this)
        , popoverName = (name) ? name : 'defaultPopover'
        , data = jq172this.data(popoverName)
        , options = typeof option == 'object' && option
      if (!data) jq172this.data(popoverName, (data = new Popover(popoverName, this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  jq172.fn.popover.Constructor = Popover

  jq172.fn.popover.defaults = jq172.extend({} , jq172.fn.tooltip.defaults, {
    placement: 'right'
  , trigger: 'click'
  , content: ''
  , template: '<div class="popover"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>'
  })


 /* POPOVER NO CONFLICT
  * =================== */

  jq172.fn.popover.noConflict = function () {
    jq172.fn.popover = old
    return this
  }

}(window.jq172);

/* ============================================================
 * bootstrap-button.js v2.3.2
 * http://twitter.github.com/bootstrap/javascript.html#buttons
 * ============================================================
 * Copyright 2012 Twitter, Inc.
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
 * ============================================================ */


!function (jq172) {

  "use strict"; // jshint ;_;


 /* BUTTON PUBLIC CLASS DEFINITION
  * ============================== */

  var Button = function (element, options) {
    this.jq172element = jq172(element)
    this.options = jq172.extend({}, jq172.fn.button.defaults, options)
  }

  Button.prototype.setState = function (state) {
    var d = 'disabled'
      , jq172el = this.jq172element
      , data = jq172el.data()
      , val = jq172el.is('input') ? 'val' : 'html'

    state = state + 'Text'
    data.resetText || jq172el.data('resetText', jq172el[val]())

    jq172el[val](data[state] || this.options[state])

    // push to event loop to allow forms to submit
    setTimeout(function () {
      state == 'loadingText' ?
        jq172el.addClass(d).attr(d, d) :
        jq172el.removeClass(d).removeAttr(d)
    }, 0)
  }

  Button.prototype.toggle = function () {
    var jq172parent = this.jq172element.closest('[data-toggle="buttons-radio"]')

    jq172parent && jq172parent
      .find('.active')
      .removeClass('active')

    this.jq172element.toggleClass('active')
  }


 /* BUTTON PLUGIN DEFINITION
  * ======================== */

  var old = jq172.fn.button

  jq172.fn.button = function (option) {
    return this.each(function () {
      var jq172this = jq172(this)
        , data = jq172this.data('button')
        , options = typeof option == 'object' && option
      if (!data) jq172this.data('button', (data = new Button(this, options)))
      if (option == 'toggle') data.toggle()
      else if (option) data.setState(option)
    })
  }

  jq172.fn.button.defaults = {
    loadingText: 'loading...'
  }

  jq172.fn.button.Constructor = Button


 /* BUTTON NO CONFLICT
  * ================== */

  jq172.fn.button.noConflict = function () {
    jq172.fn.button = old
    return this
  }


 /* BUTTON DATA-API
  * =============== */

  jq172(document).bind('click.button.data-api', '[data-toggle^=button]', function (e) {
    var jq172btn = jq172(e.target)
    if (!jq172btn.hasClass('btn')) jq172btn = jq172btn.closest('.btn')
    jq172btn.button('toggle')
  })

}(window.jq172);