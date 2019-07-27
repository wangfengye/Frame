//
// Created by 10404 on 2019/7/8.
//
#include <stdint.h>

namespace art {
    namespace mirror {
        class Object {
            uint32_t klass_;
            uint32_t monitor_;
        };

        class ArtMethod : public Object {
        public:

            uint32_t declaring_class;
            // Access flags; low 16 bits are defined by spec.
            uint32_t access_flags_;

            /* Dex file fields. The defining dex file is available via declaring_class_->dex_cache_ */

            // Offset to the CodeItem.
            uint32_t dex_code_item_offset_;

            // Index into method_ids of the dex file associated with this method.
            uint32_t dex_method_index_;

            /* End of dex file fields. */

            // Entry within a dispatch table for this method. For static/direct methods the index is into
            // the declaringClass.directMethods, for virtual methods the vtable and for interface methods the
            // ifTable.
            uint32_t method_index_;
            uint64_t dex_cache_resolved_methods_;
            // Short cuts to declaring_class_->dex_cache_ member for fast compiled code access.
            uint64_t dex_cache_resolved_types_;
        };
    }
}