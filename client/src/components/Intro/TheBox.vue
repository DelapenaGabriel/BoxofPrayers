<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as THREE from 'three'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js'

const props = defineProps({
  isOpen: Boolean,
})

const emit = defineEmits(['box-click'])

const containerRef = ref(null)

// Scene variables
let scene, camera, renderer
let boxModel, boxLid, innerLight, mixer
let animationId
let time = 0
let particles
// Raycaster for interaction
// Raycaster for interaction
const raycaster = new THREE.Raycaster()
const mouse = new THREE.Vector2()

// Interaction State
let distinctMouse = new THREE.Vector2() // For parallax
let targetRotationX = 0
let targetRotationY = 0
let currentRotationX = 0
let currentRotationY = 0
const windowHalfX = window.innerWidth / 2
const windowHalfY = window.innerHeight / 2

// Material constants - Gold/Metallic look
const boxMaterial = new THREE.MeshStandardMaterial({
  color: 0xffd700, // Gold
  metalness: 1.0,
  roughness: 0.3,
  emissive: 0xaa8800,
  emissiveIntensity: 0.1,
  side: THREE.DoubleSide,
})

const initScene = () => {
  if (!containerRef.value) return

  // 1. Scene
  scene = new THREE.Scene()
  // Transparent background so the IntroOverlay gradient shows through
  scene.background = null

  // 2. Camera
  const width = containerRef.value.clientWidth
  const height = containerRef.value.clientHeight
  camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000)
  camera.position.z = 6
  camera.position.y = 1.5

  // 3. Renderer
  renderer = new THREE.WebGLRenderer({ alpha: true, antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2))
  containerRef.value.appendChild(renderer.domElement)

  // 4. Lights
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.5)
  scene.add(ambientLight)

  const dirLight = new THREE.DirectionalLight(0xffdbac, 2) // Sun/Warm tint
  dirLight.position.set(5, 5, 5)
  scene.add(dirLight)

  const blueRim = new THREE.SpotLight(0x0088ff, 5)
  blueRim.position.set(-5, 2, -5)
  scene.add(blueRim)

  // Inner Point Light (The "God Light" - starts dim)
  innerLight = new THREE.PointLight(0xffaa00, 0, 10)
  innerLight.position.set(0, 0.5, 0)
  scene.add(innerLight)

  // 5. Load Model
  loadModel()

  // 6. Particles
  createParticles()

  // 7. Interaction
  // 7. Interaction
  window.addEventListener('pointerdown', onPointerDown)
  window.addEventListener('mousemove', onDocumentMouseMove)
  window.addEventListener('touchmove', onDocumentTouchMove, { passive: false })

  // 8. Animation Loop
  animate()
}

const loadModel = () => {
  const loader = new GLTFLoader()
  loader.load(
    '/intro/pirate_chest.glb',
    (gltf) => {
      boxModel = gltf.scene

      // Auto-center and scale
      const box = new THREE.Box3().setFromObject(boxModel)
      const center = box.getCenter(new THREE.Vector3())
      const size = box.getSize(new THREE.Vector3())

      // Reset position to center geometry
      boxModel.position.x += boxModel.position.x - center.x
      boxModel.position.y += boxModel.position.y - center.y
      boxModel.position.z += boxModel.position.z - center.z

      // Scale to fit roughly size 2
      const maxDim = Math.max(size.x, size.y, size.z)
      const scale = 2.5 / maxDim
      boxModel.scale.set(scale, scale, scale)

      // Traverse to find the lid and fix materials if needed
      boxModel.traverse((child) => {
        if (child.isMesh) {
          child.castShadow = true
          child.receiveShadow = true
          // Keep original materials but maybe enhance them?
          // For now, let's stick with the GLB materials unless they are missing
          if (child.material) {
            child.material.envMapIntensity = 1
          }
        }
        // Attempt to find the lid part by common names if we want to rotate it specifically
        // Common names: Lid, Top, Door
        const name = child.name.toLowerCase()
        if (name.includes('lid') || name.includes('top')) {
          boxLid = child
        }
      })

      // If no specific lid found, we might rotate the whole top half or just rely on the open state provided
      // Since user said "half open treasure box", maybe it's static geometry?
      // We will float the whole box for now.

      scene.add(boxModel)
    },
    undefined,
    (error) => {
      console.error('An error happened loading the GLB:', error)
    },
  )
}

const createParticles = () => {
  const particleCount = 200
  const geometry = new THREE.BufferGeometry()
  const positions = new Float32Array(particleCount * 3)
  const colors = new Float32Array(particleCount * 3)
  const sizes = new Float32Array(particleCount)

  const color = new THREE.Color()

  for (let i = 0; i < particleCount; i++) {
    positions[i * 3] = (Math.random() - 0.5) * 2 // x
    positions[i * 3 + 1] = (Math.random() - 0.5) * 1 // y
    positions[i * 3 + 2] = (Math.random() - 0.5) * 2 // z

    // Golden/Fire colors
    color.setHSL(0.1 + Math.random() * 0.1, 1.0, 0.5 + Math.random() * 0.2)
    colors[i * 3] = color.r
    colors[i * 3 + 1] = color.g
    colors[i * 3 + 2] = color.b

    sizes[i] = Math.random() * 0.1
  }

  geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3))
  geometry.setAttribute('color', new THREE.BufferAttribute(colors, 3))
  geometry.setAttribute('size', new THREE.BufferAttribute(sizes, 1))

  // Create a glow texture procedurally or use a simple dot
  const sprite = new THREE.TextureLoader().load('/three/glow.png') // Assuming we might not have one, let's make a simple shader or canvas texture?
  // Let's use a simple canvas for the dot texture to avoid 404s
  const canvas = document.createElement('canvas')
  canvas.width = 32
  canvas.height = 32
  const context = canvas.getContext('2d')
  const gradient = context.createRadialGradient(16, 16, 0, 16, 16, 16)
  gradient.addColorStop(0, 'rgba(255,255,255,1)')
  gradient.addColorStop(0.2, 'rgba(255,255,255,1)')
  gradient.addColorStop(0.4, 'rgba(255,215,0,0.5)')
  gradient.addColorStop(1, 'rgba(0,0,0,0)')
  context.fillStyle = gradient
  context.fillRect(0, 0, 32, 32)
  const texture = new THREE.CanvasTexture(canvas)

  const material = new THREE.PointsMaterial({
    size: 0.1,
    map: texture,
    vertexColors: true,
    blending: THREE.AdditiveBlending,
    depthWrite: false,
    transparent: true,
    opacity: 0,
  })

  particles = new THREE.Points(geometry, material)
  scene.add(particles)
}

const onPointerDown = (event) => {
  event.preventDefault()

  mouse.x = (event.clientX / window.innerWidth) * 2 - 1
  mouse.y = -(event.clientY / window.innerHeight) * 2 + 1

  raycaster.setFromCamera(mouse, camera)

  if (boxModel) {
    const intersects = raycaster.intersectObjects([boxModel], true)
    if (intersects.length > 0) {
      // Trigger open
      emit('box-click')
    }
  }
}

const onDocumentMouseMove = (event) => {
  distinctMouse.x = event.clientX - windowHalfX
  distinctMouse.y = event.clientY - windowHalfY

  targetRotationX = distinctMouse.x * 0.0005
  targetRotationY = distinctMouse.y * 0.0005
}

const onDocumentTouchMove = (event) => {
  if (event.touches.length === 1) {
    // event.preventDefault() // prevent scrolling? maybe not if we want them to be able to scroll past?
    // Actually IntroOverlay is fixed/fullscreen so scrolling might not be an issue or desired.

    distinctMouse.x = event.touches[0].pageX - windowHalfX
    distinctMouse.y = event.touches[0].pageY - windowHalfY

    targetRotationX = distinctMouse.x * 0.001 // Slightly more sensitive for touch
    targetRotationY = distinctMouse.y * 0.001
  }
}

const animate = () => {
  animationId = requestAnimationFrame(animate)
  time += 0.01

  if (boxModel) {
    // Idle Float
    boxModel.position.y = Math.sin(time) * 0.1
    boxModel.rotation.y = Math.sin(time * 0.5) * 0.1
  }

  // Particles Animation (Swirl)
  if (particles) {
    const positions = particles.geometry.attributes.position.array
    for (let i = 0; i < 200; i++) {
      // Swirl upwards
      positions[i * 3 + 1] += 0.01 // Y
      const x = positions[i * 3]
      const z = positions[i * 3 + 2]
      // Rotate around Y
      const speed = 0.02
      positions[i * 3] = x * Math.cos(speed) - z * Math.sin(speed)
      positions[i * 3 + 2] = x * Math.sin(speed) + z * Math.cos(speed)

      // Reset if too high
      if (positions[i * 3 + 1] > 2) {
        positions[i * 3 + 1] = -0.5
      }
    }
    particles.geometry.attributes.position.needsUpdate = true

    // Fade in particles when open
    if (props.isOpen) {
      particles.material.opacity += (1 - particles.material.opacity) * 0.05
    } else {
      particles.material.opacity += (0 - particles.material.opacity) * 0.1
    }
  }

  // Open Animation
  if (props.isOpen) {
    // If we found a lid, rotate it
    if (boxLid) {
      const targetRot = -1.5
      // Assuming X axis is hinge
      boxLid.rotation.x += (targetRot - boxLid.rotation.x) * 0.05
    }

    // Light Burst
    innerLight.intensity += (50 - innerLight.intensity) * 0.05
    innerLight.distance += (20 - innerLight.distance) * 0.05

    // Move camera slightly based on open state
    camera.position.z += (4.5 - camera.position.z) * 0.02
  }

  // Parallax / Interaction Rotation
  // Smoothly interpolate current rotation to target
  currentRotationX += (targetRotationX - currentRotationX) * 0.05
  currentRotationY += (targetRotationY - currentRotationY) * 0.05

  // Apply to camera group or camera itself
  // Rotating the camera around the center (0,0,0)
  camera.position.x += (currentRotationX * 5 - camera.position.x) * 0.05
  camera.position.y += (-currentRotationY * 5 + (props.isOpen ? 1 : 1.5) - camera.position.y) * 0.05
  camera.lookAt(scene.position)

  renderer.render(scene, camera)
}

// Window Resize
const handleResize = () => {
  if (!containerRef.value || !camera || !renderer) return
  const width = containerRef.value.clientWidth
  const height = containerRef.value.clientHeight

  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)

  // Adjust camera distance for mobile
  // Mobile needs to be further away to fit the object
  if (width < 768) {
    camera.position.z = 9 // Move back for mobile
    camera.position.y = 1 // Lower slightly
  } else {
    camera.position.z = 6 // Standard desktop
    camera.position.y = 1.5
  }
}

onMounted(() => {
  initScene()
  // Initial check
  handleResize()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('pointerdown', onPointerDown)
  window.removeEventListener('mousemove', onDocumentMouseMove)
  window.removeEventListener('touchmove', onDocumentTouchMove)
  cancelAnimationFrame(animationId)
  if (renderer) renderer.dispose()
})
</script>

<template>
  <div ref="containerRef" class="three-container"></div>
</template>

<style scoped>
.three-container {
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 2; /* Interactable layer */
  pointer-events: auto; /* Ensure clicks pass to the canvas for raycasting */
  /* If the text is ON TOP, we need to make sure text allows clicks through?
     No, text needs to be clickable too.
     ThreeJS raycaster works on the canvas.
     If text covers the box, we can't click the box.
     We should ensure the text is positioned BELOW the box visually or allows interactions.
  */
}
</style>
